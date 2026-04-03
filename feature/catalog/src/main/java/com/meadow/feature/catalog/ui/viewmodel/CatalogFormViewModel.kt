package com.meadow.feature.catalog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.ui.state.ReferenceDataProvider
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class CatalogFormViewModel<S>(
    protected val seriesRepository: SeriesRepositoryContract,
    val referenceDataProvider: ReferenceDataProvider,
    protected val aiManager: AiManager
) : ViewModel() {

    /* ─── UI STATE ─────────────────────────── */
    protected abstract val _uiState: MutableStateFlow<S>

    protected abstract fun copyState(
        state: S,
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>
    ): S

    /* ───────────── FIELD HELPERS ───────────── */

    protected fun updateFieldInternal(updated: FieldWithValue) {
        val state = _uiState.value

        // Block edits while saving
        if (isSaving(state)) return

        val newFields = getFields(state).map { f ->
            if (f.definition.id == updated.definition.id) updated else f
        }

        _uiState.value = markDirty(
            copyState(
                state = state,
                fields = newFields,
                seriesId = getSeriesId(state),
                seriesSharedFieldIds = getSeriesSharedFieldIds(state)
            )
        )
    }

    protected fun updateSeriesInternal(seriesId: String?) {
        val state = _uiState.value
        if (isSaving(state)) return

        _uiState.value = markDirty(
            copyState(
                state = state,
                fields = getFields(state),
                seriesId = seriesId,
                seriesSharedFieldIds = getSeriesSharedFieldIds(state)
            )
        )
    }

    protected fun toggleSeriesFieldInternal(fieldId: String, makeSeries: Boolean) {
        val state = _uiState.value
        if (isSaving(state)) return

        val seriesId = getSeriesId(state) ?: return

        viewModelScope.launch {
            if (makeSeries) {
                seriesRepository.addSeriesCatalogField(seriesId, fieldId)
            } else {
                seriesRepository.removeSeriesCatalogField(seriesId, fieldId)
            }

            _uiState.update { s ->
                val currentShared = getSeriesSharedFieldIds(s)
                val nextShared =
                    if (makeSeries) currentShared + fieldId else currentShared - fieldId

                markDirty(
                    copyState(
                        state = s,
                        fields = getFields(s),
                        seriesId = seriesId,
                        seriesSharedFieldIds = nextShared
                    )
                )
            }
        }
    }

    /* ───────────── AI FIELD HELPERS ───────────── */

    fun runBudForField(
        field: FieldWithValue,
        mode: AiMode
    ) {
        val state = _uiState.value
        if (isSaving(state)) return

        viewModelScope.launch {
            val action = mode.toFieldAiAction()

            val response = aiManager.bud.runFieldAction(
                currentText = field.value?.rawValue?.toString(),
                action = action,
                fieldLabel = field.definition.labelKey
            )

            updateFieldInternal(
                field.copy(
                    value = FieldValue(
                        fieldId = field.definition.id,
                        ownerItemId = field.value?.ownerItemId ?: "",
                        rawValue = response.content
                    )
                )
            )
        }
    }

    /* ───────────── DIRTY / LOCK HELPERS ───────────── */

    protected open fun isSaving(state: S): Boolean = false
    protected open fun markDirty(state: S): S = state

    /* ───────────── STATE ACCESSORS ───────────── */

    protected abstract fun getFields(state: S): List<FieldWithValue>
    protected abstract fun getSeriesId(state: S): String?
    protected abstract fun getSeriesSharedFieldIds(state: S): Set<String>
}
