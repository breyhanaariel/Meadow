package com.meadow.feature.catalog.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogScope
import com.meadow.feature.catalog.domain.model.CatalogType
import com.meadow.feature.catalog.domain.usecase.DeleteCatalogItemUseCase
import com.meadow.feature.catalog.domain.usecase.ObserveCatalogUseCase
import com.meadow.feature.catalog.sync.drive.CatalogDriveBackupCoordinator
import com.meadow.feature.catalog.sync.sheets.CatalogSheetsBackupCoordinator
import com.meadow.feature.catalog.ui.state.*
import com.meadow.feature.catalog.ui.util.*
import com.meadow.feature.catalog.ui.util.readTitleOrNull
import com.meadow.feature.catalog.ui.util.readType
import com.meadow.feature.catalog.ui.util.searchBlob
import com.meadow.feature.project.api.ProjectSelector
import com.meadow.feature.project.api.ProjectSelectorItem
import com.meadow.feature.project.api.SeriesSelector
import com.meadow.feature.project.api.SeriesSelectorItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.String
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
@HiltViewModel
class CatalogListViewModel @Inject constructor(
    private val observeCatalog: ObserveCatalogUseCase,
    private val projectSelector: ProjectSelector,
    private val seriesSelector: SeriesSelector,
    private val deleteCatalogItem: DeleteCatalogItemUseCase,
    private val sheetsBackupCoordinator: CatalogSheetsBackupCoordinator,
    private val driveBackupCoordinator: CatalogDriveBackupCoordinator,
    private val app: Application
) : ViewModel() {


    /* ─── GOOGLE SHEETS BACKUP ───────────────────── */

    val sheetsEvents = sheetsBackupCoordinator.events

    fun backupCatalogItemToSheets(itemId: String) {
        viewModelScope.launch {
            sheetsBackupCoordinator.backupSingle(itemId)
        }
    }
    fun backupCatalogItemToDrive(itemId: String) {
        viewModelScope.launch {
            driveBackupCoordinator.backupCatalog(itemId)
        }
    }
    fun backupCatalogItem(itemId: String) {
        viewModelScope.launch {
            runCatching {
                driveBackupCoordinator.backupCatalog(itemId)

                sheetsBackupCoordinator.backupSingle(itemId)
            }
        }
    }


    private val _uiState =
        MutableStateFlow(
            CatalogListUiState(
                isLoading = true,
                filterOptions = CatalogFilterOptions(types = emptyList())
            )
        )
    val uiState: StateFlow<CatalogListUiState> =
        _uiState.asStateFlow()

    private var domainAllItems: List<CatalogItem> = emptyList()

    /* ─── PICKERS ───────────────────── */

    val projectPickerItems: StateFlow<List<ProjectSelectorItem>> =
        projectSelector.observeAvailableProjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _seriesPickerItems =
        MutableStateFlow<List<SeriesSelectorItem>>(emptyList())
    val seriesPickerItems: StateFlow<List<SeriesSelectorItem>> =
        _seriesPickerItems.asStateFlow()

    init {
        observeItems()
        updateSeriesForScope(CatalogListScope.Global)
    }

    /* ─── SCOPE ───────────────────── */

    fun setScope(scope: CatalogListScope) {
        val s = _uiState.value
        if (s.scope == scope) return

        _uiState.value = s.copy(
            scope = scope,
            items = applyFilters(s.allItems, scope, s.searchQuery, s.sortMode, s.filters)
        )

        updateSeriesForScope(scope)
    }

    private fun updateSeriesForScope(scope: CatalogListScope) {
        viewModelScope.launch {
            val flow =
                if (scope is CatalogListScope.Project)
                    seriesSelector.observeAvailableSeries(scope.projectId)
                else
                    seriesSelector.observeAvailableSeries(null)

            flow.collect { _seriesPickerItems.value = it }
        }
    }

    /* ─── OBSERVE CATALOG ───────────────────── */

    private fun observeItems() {
        viewModelScope.launch {
            uiState
                .map { it.scope.toDomainScope() }
                .distinctUntilChanged()
                .flatMapLatest { scope ->
                    observeCatalog(scope)
                }
                .collect { items ->
                    domainAllItems = items

                    val s = _uiState.value
                    val uiAll = items.map { it.toUiModel(app) }
                    val availableTypes =
                        uiAll.map { it.type }.distinct().sortedBy { it.name }

                    _uiState.value = s.copy(
                        isLoading = false,
                        allItems = uiAll,
                        filterOptions = CatalogFilterOptions(types = availableTypes),
                        items = applyFilters(
                            all = uiAll,
                            scope = s.scope,
                            query = s.searchQuery,
                            sort = s.sortMode,
                            filters = s.filters
                        )
                    )
                }
        }
    }


    fun onQueryChange(query: String) =
        update { it.copy(searchQuery = query) }

    fun updateSort(sort: CatalogListSort) =
        update { it.copy(sortMode = sort) }

    fun updateViewMode(mode: CatalogListViewMode) =
        update { it.copy(viewMode = mode) }

    fun toggleTypeFilter(type: CatalogType) =
        update {
            val set = it.filters.types
            it.copy(filters = it.filters.copy(
                types = if (type in set) set - type else set + type
            ))
        }

    fun clearTypeFilters() =
        update { it.copy(filters = it.filters.copy(types = emptySet())) }

    fun requestDelete(item: CatalogItemUiModel) =
        update { it.copy(pendingDeleteId = item.id) }

    fun cancelDelete() =
        update { it.copy(pendingDeleteId = null) }

    fun confirmDelete() {
        val id = _uiState.value.pendingDeleteId ?: return
        viewModelScope.launch {
            deleteCatalogItem(id)
            cancelDelete()
        }
    }

    fun backupAllVisibleToSheets() {
        viewModelScope.launch {
            uiState.value.items.forEach { item ->
                sheetsBackupCoordinator.backupSingle(item.id)
            }
        }
    }

    /* ─── FILTER LOGIC ───────────────────── */

    private fun applyFilters(
        all: List<CatalogItemUiModel>,
        scope: CatalogListScope,
        query: String,
        sort: CatalogListSort,
        filters: CatalogListFilters
    ): List<CatalogItemUiModel> {

        var result = when (scope) {
            CatalogListScope.Global -> all
            is CatalogListScope.Project ->
                all.filter { it.projectId == scope.projectId }
            is CatalogListScope.Series ->
                all.filter { it.seriesId == scope.seriesId }
        }

        if (query.isNotBlank()) {
            result = result.filter {
                it.searchBlob.contains(query, ignoreCase = true)
            }
        }


        if (filters.types.isNotEmpty()) {
            result = result.filter {
                it.type in filters.types
            }
        }

        return when (sort) {
            CatalogListSort.TITLE_ASC ->
                result.sortedBy { (it.title ?: "").lowercase() }

            CatalogListSort.TITLE_DESC ->
                result.sortedByDescending  { (it.title ?: "").lowercase() }

            CatalogListSort.CREATED_ASC ->
                result.sortedBy { it.createdAt }

            CatalogListSort.CREATED_DESC ->
                result.sortedByDescending { it.createdAt }

            CatalogListSort.UPDATED_ASC ->
                result.sortedBy { it.updatedAt }

            CatalogListSort.UPDATED_DESC ->
                result.sortedByDescending { it.updatedAt }
        }
    }

    private inline fun update(block: (CatalogListUiState) -> CatalogListUiState) {
        val s = _uiState.value
        val updated = block(s)
        _uiState.value = updated.copy(
            items = applyFilters(
                all = updated.allItems,
                scope = updated.scope,
                query = updated.searchQuery,
                sort = updated.sortMode,
                filters = updated.filters
            )
        )
    }

    fun updateFilters(filters: CatalogListFilters) =
        update { it.copy(filters = filters) }
}
private fun CatalogListScope.toDomainScope(): CatalogScope =
    when (this) {
        CatalogListScope.Global -> CatalogScope.Global
        is CatalogListScope.Project -> CatalogScope.Project(projectId)
        is CatalogListScope.Series -> CatalogScope.Series(seriesId)
    }


private fun CatalogItem.toUiModel(
    context: Context
): CatalogItemUiModel {
    val type = readType()

    return CatalogItemUiModel(
        id = id,
        schemaId = schemaId,
        projectId = projectId,
        seriesId = seriesId,
        title = readTitleOrNull(),
        type = type,
        iconResId = type.iconRes(),
        schemaLabel = context.getString(type.labelRes()),
        createdAt = createdAt,
        updatedAt = updatedAt,
        searchBlob = searchBlob()
    )

}
