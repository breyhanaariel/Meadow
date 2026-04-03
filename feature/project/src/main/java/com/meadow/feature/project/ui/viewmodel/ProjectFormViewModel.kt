package com.meadow.feature.project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.feature.project.aicontext.domain.AiMode
import com.meadow.feature.project.ui.state.CreateProjectUiState
import com.meadow.feature.project.ui.state.EditProjectUiState
import com.meadow.feature.project.ui.state.ProjectFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class ProjectFormViewModel<S : ProjectFormState>(
    protected val aiManager: AiManager
) : ViewModel() {

    /* ─── UI STATE ─────────────────────────── */
    protected abstract val _uiState: MutableStateFlow<S>
    val uiState: StateFlow<S> get() = _uiState.asStateFlow()

    /* ─── REQUIRED BY SUBCLASSES ───────────── */
    protected abstract fun copyState(
        state: S,
        fields: List<FieldWithValue> = state.fields,
        seriesId: String? = state.seriesId,
        seriesSharedFieldIds: Set<String> = state.seriesSharedFieldIds
    ): S

    /** Subclasses decide whether an edit should mark the form as dirty. */
    abstract fun markDirty(state: S): S

    /** Subclasses expose saving status (Create uses isSaving, Edit may use isSaving too). */
    abstract fun isSaving(state: S): Boolean

    /* ───────────── SHARED FIELD HELPERS ───────────── */

    fun updateFieldInternal(value: FieldValue) {
        _uiState.update { state ->
            if (isSaving(state)) state
            else markDirty(
                copyState(
                    state,
                    fields = state.fields.map { f ->
                        if (f.definition.id == value.fieldId) f.copy(value = value) else f
                    }
                )
            )
        }
    }

    fun updateCoverInternal(path: String) {
        _uiState.update { state ->
            val ownerId =
                when (state) {
                    is CreateProjectUiState -> state.draftProjectId
                    is EditProjectUiState -> state.projectId
                    else -> ""
                }

            markDirty(
                copyState(
                    state,
                    fields = state.fields.map { f ->
                        if (f.definition.metadata?.get("role") == "cover") {
                            val nextValue =
                                (f.value ?: FieldValue(
                                    fieldId = f.definition.id,
                                    ownerItemId = ownerId,
                                    rawValue = path
                                )).copy(rawValue = path)

                            f.copy(value = nextValue)
                        } else f
                    }
                )
            )
        }
    }

    fun updateSeriesInternal(seriesId: String?) {
        _uiState.update { state ->
            markDirty(copyState(state, seriesId = seriesId))
        }
    }

    fun toggleSeriesFieldInternal(fieldId: String, makeSeries: Boolean) {
        _uiState.update { state ->
            val updated =
                if (makeSeries) state.seriesSharedFieldIds + fieldId
                else state.seriesSharedFieldIds - fieldId

            markDirty(copyState(state, seriesSharedFieldIds = updated))
        }
    }

    /* ───────────── BUD FIELD HELPERS ───────────── */

    fun runBudForField(
        field: FieldWithValue,
        mode: AiMode
    ) {
        viewModelScope.launch {
            val action = mode.toFieldAiAction()

            val response = aiManager.bud.runFieldAction(
                currentText = field.value?.rawValue?.toString(),
                action = action,
                fieldLabel = field.definition.labelKey
            )
            updateFieldInternal(
                FieldValue(
                    fieldId = field.definition.id,
                    ownerItemId = field.value?.ownerItemId ?: "",
                    rawValue = response.content
                )
            )
        }
    }
}
