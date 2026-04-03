package com.meadow.feature.catalog.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.events.UiMessage
import com.meadow.core.ui.state.ReferenceDataProvider
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import com.meadow.feature.catalog.ui.state.EditCatalogItemUiState
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class EditCatalogItemViewModel @Inject constructor(
    private val catalogRepo: CatalogRepositoryContract,
    private val seriesRepo: SeriesRepositoryContract,
    referenceDataProvider: ReferenceDataProvider,
    aiManager: AiManager
) : CatalogFormViewModel<EditCatalogItemUiState>(
    seriesRepository = seriesRepo,
    aiManager = aiManager,
    referenceDataProvider = referenceDataProvider
) {

    /* ─── UI STATE ─────────────────────────── */
    override val _uiState =
        MutableStateFlow(EditCatalogItemUiState(isLoading = true))
    val uiState: StateFlow<EditCatalogItemUiState> = _uiState.asStateFlow()

    /* ─── SNACKBARS ────────────────────────── */
    private val _uiMessages = MutableSharedFlow<UiMessage>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiMessages = _uiMessages.asSharedFlow()

    private var loadedItemId: String? = null

    /* ─── REQUIRED BASE ────────────────────── */
    override fun copyState(
        state: EditCatalogItemUiState,
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>
    ): EditCatalogItemUiState =
        state.copy(
            fields = fields,
            seriesId = seriesId,
            seriesSharedFieldIds = seriesSharedFieldIds
        )

    override fun getFields(state: EditCatalogItemUiState) = state.fields
    override fun getSeriesId(state: EditCatalogItemUiState) = state.seriesId
    override fun getSeriesSharedFieldIds(state: EditCatalogItemUiState) =
        state.seriesSharedFieldIds

    /* ─── LOAD ─────────────────────────────── */
    fun load(itemId: String) {
        if (loadedItemId == itemId && !_uiState.value.isLoading) return
        loadedItemId = itemId

        viewModelScope.launch {
            val item = catalogRepo.getCatalogItemById(itemId)
            if (item == null) {
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.load_failed))
                _uiState.value = EditCatalogItemUiState(isLoading = false)
                return@launch
            }

            val sharedFieldIds =
                item.seriesId?.let { seriesRepo.getSeriesById(it)?.sharedCatalogFieldIds?.toSet() }
                    ?: emptySet()

            _uiState.value = EditCatalogItemUiState(
                isLoading = false,
                catalogItemId = item.id,
                fields = item.fields,
                seriesId = item.seriesId,
                seriesSharedFieldIds = sharedFieldIds
            )
        }
    }

    /* ─── FIELD EDITS ──────────────────────── */
    fun updateField(updated: FieldWithValue) {
        _uiState.update { s ->
            s.copy(
                fields = s.fields.map {
                    if (it.definition.id == updated.definition.id) updated else it
                },
                hasUnsavedChanges = true
            )
        }
    }

    fun updateFieldValue(value: FieldValue) {
        val current = _uiState.value.fields.firstOrNull {
            it.definition.id == value.fieldId
        } ?: return

        updateField(
            current.copy(value = value)
        )
    }


    fun toggleSeriesField(fieldId: String, makeSeries: Boolean) =
        toggleSeriesFieldInternal(fieldId, makeSeries)

    /* ─── SAVE / DELETE ────────────────────── */
    fun save() {
        val s = _uiState.value
        val itemId = s.catalogItemId ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }

                val existing = catalogRepo.getCatalogItemById(itemId) ?: return@launch
                catalogRepo.saveCatalogItem(
                    existing.copy(
                        fields = s.fields,
                        seriesId = s.seriesId,
                        updatedAt = System.currentTimeMillis()
                    )
                )

                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.save_success))
                _uiState.update { it.copy(hasUnsavedChanges = false) }

            } catch (_: Exception) {
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.save_failed))
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun delete() {
        val itemId = _uiState.value.catalogItemId ?: return

        viewModelScope.launch {
            try {
                catalogRepo.deleteCatalogItem(itemId)
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.delete_success))
            } catch (_: Exception) {
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.delete_failed))
            }
        }
    }
}
