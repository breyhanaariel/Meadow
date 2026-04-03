package com.meadow.feature.catalog.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.events.UiMessage
import com.meadow.core.ui.state.ReferenceDataProvider
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogSyncMeta
import com.meadow.feature.catalog.domain.model.CatalogType
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import com.meadow.feature.catalog.internal.schema.CatalogSchema
import com.meadow.feature.catalog.internal.schema.CatalogSchemaRegistry
import com.meadow.feature.catalog.ui.state.CreateCatalogItemUiState
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/* ─── ONE OFF UI EVENTS ─────────────────────────── */

sealed interface CreateCatalogItemEvent {
    object NavigateBack : CreateCatalogItemEvent
}

@HiltViewModel
class CreateCatalogItemViewModel @Inject constructor(
    private val catalogRepo: CatalogRepositoryContract,
    private val projectRepo: ProjectRepositoryContract,
    private val seriesRepo: SeriesRepositoryContract,
    private val schemaRegistry: CatalogSchemaRegistry,
    referenceDataProvider: ReferenceDataProvider,
    aiManager: AiManager
) : CatalogFormViewModel<CreateCatalogItemUiState>(
    seriesRepository = seriesRepo,
    aiManager = aiManager,
    referenceDataProvider = referenceDataProvider
) {

    /* ─── UI STATE ─────────────────────────── */
    override val _uiState = MutableStateFlow(CreateCatalogItemUiState())
    val uiState: StateFlow<CreateCatalogItemUiState> = _uiState.asStateFlow()

    private var currentProjectId: String? = null

    /* ─── SNACKBAR SYSTEM ───────────────────── */
    private val _uiMessages = MutableSharedFlow<UiMessage>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiMessages = _uiMessages.asSharedFlow()

    /* ─── NAVIGATION EVENTS ─────────────────── */
    private val _events = MutableSharedFlow<CreateCatalogItemEvent>()
    val events = _events.asSharedFlow()

    /* ─── SCHEMAS ─────────────────────────── */
    private val _schemas = MutableStateFlow<List<CatalogSchema>>(emptyList())
    val schemas: StateFlow<List<CatalogSchema>> = _schemas

    val availableCatalogTypes: StateFlow<List<CatalogType>> =
        schemas
            .map { list ->
                list
                    .map { it.schemaId.substringBefore("_") }
                    .distinct()
                    .map { CatalogType.fromKey(it) }
                    .filter { it != CatalogType.UNKNOWN }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    /* ─── REQUIRED BASE ────────────────────── */
    override fun copyState(
        state: CreateCatalogItemUiState,
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>
    ): CreateCatalogItemUiState =
        state.copy(
            fields = fields,
            seriesId = seriesId,
            seriesSharedFieldIds = seriesSharedFieldIds
        )

    override fun getFields(state: CreateCatalogItemUiState): List<FieldWithValue> = state.fields
    override fun getSeriesId(state: CreateCatalogItemUiState): String? = state.seriesId
    override fun getSeriesSharedFieldIds(state: CreateCatalogItemUiState): Set<String> =
        state.seriesSharedFieldIds

    /* ─── INITIAL LOAD ─────────────────────── */
    fun initialize(projectId: String, seriesId: String?) {
        if (currentProjectId == projectId && _uiState.value.seriesId == seriesId) return

        currentProjectId = projectId
        _uiState.update { it.copy(seriesId = seriesId) }

        viewModelScope.launch {
            schemaRegistry.initializeIfNeeded()

            val project = projectRepo.getProjectById(projectId)
            val projectType = project?.type

            _uiState.update {
                it.copy(projectType = projectType)
            }

            _schemas.value =
                schemaRegistry.getCatalogItemSchemasForProject(projectType?.key ?: "")

            val sharedIds =
                seriesId?.let { id ->
                    seriesRepo.getSeriesById(id)
                        ?.sharedCatalogFieldIds
                        ?.toSet()
                } ?: emptySet()

            _uiState.update {
                it.copy(seriesSharedFieldIds = sharedIds)
            }
        }
    }

    /* ─── SCHEMA SELECTOR ──────────────────── */

    fun selectCatalogType(type: CatalogType) {
        val schemasForType = schemaRegistry.getSchemasForType(type.key)

        _schemas.value = schemasForType

        when {
            schemasForType.size == 1 -> {
                _uiState.update {
                    it.copy(catalogType = type)
                }
                selectSchema(schemasForType.first().schemaId)
            }

            else -> {
                _uiState.update {
                    it.copy(
                        catalogType = type,
                        schemaId = null
                    )
                }
            }
        }
    }

    private fun loadSchemasForType(type: CatalogType) {
        _schemas.value =
            schemaRegistry.getSchemasForType(type.key)
    }

    fun selectSchema(schemaId: String) {
        val schema = schemaRegistry.getSchema(schemaId) ?: return
        val itemId = UUID.randomUUID().toString()

        val fields = schema.fields.map { def ->
            FieldWithValue(
                definition = def,
                value = FieldValue(
                    fieldId = def.id,
                    ownerItemId = itemId,
                    rawValue = def.defaultValue
                )
            )
        }

        _uiState.update {
            it.copy(
                schemaId = schemaId,
                fields = fields
            )
        }
    }

    /* ─── FIELD UPDATES ────────────────────── */

    fun updateField(updated: FieldWithValue) {
        _uiState.update { s ->
            s.copy(
                fields = s.fields.map {
                    if (it.definition.id == updated.definition.id) updated else it
                }
            )
        }
    }

    fun updateFieldValue(value: FieldValue) {
        val state = _uiState.value
        val field = state.fields.firstOrNull { it.definition.id == value.fieldId } ?: return
        updateField(field.copy(value = value))
    }

    /* ─── SERIES FIELD SHARING ─────────────── */
    fun toggleSeriesField(fieldId: String, makeSeries: Boolean) {
        val seriesId = _uiState.value.seriesId ?: return

        _uiState.update {
            val next =
                if (makeSeries) it.seriesSharedFieldIds + fieldId
                else it.seriesSharedFieldIds - fieldId
            it.copy(seriesSharedFieldIds = next)
        }

        viewModelScope.launch {
            try {
                if (makeSeries) {
                    seriesRepo.addSeriesCatalogField(seriesId, fieldId)
                } else {
                    seriesRepo.removeSeriesCatalogField(seriesId, fieldId)
                }
            } catch (_: Exception) { }
        }
    }

    /* ─── SAVE ────────────────────────────── */
    fun saveCatalogItem() {
        val s = _uiState.value
        val schemaId = s.schemaId ?: return
        val now = System.currentTimeMillis()

        val itemId =
            s.fields.firstOrNull()?.value?.ownerItemId ?: UUID.randomUUID().toString()

        val item = CatalogItem(
            id = itemId,
            schemaId = schemaId,
            projectId = currentProjectId,
            seriesId = s.seriesId,
            fields = s.fields,
            createdAt = now,
            updatedAt = now,
            syncMeta = CatalogSyncMeta(
                localVersion = 1,
                localUpdatedAt = now,
                isDirty = true
            )
        )

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }
                catalogRepo.saveCatalogItem(item)
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.save_success))
                _events.emit(CreateCatalogItemEvent.NavigateBack)
            } catch (_: Exception) {
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.save_failed))
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
