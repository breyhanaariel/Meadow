package com.meadow.feature.project.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.feature.project.domain.model.ProjectFieldHistory
import com.meadow.feature.project.domain.registry.ProjectTemplateRegistry
import com.meadow.feature.project.domain.repository.ProjectFieldHistoryContract
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.ui.state.ProjectFieldHistoryGroupUi
import com.meadow.feature.project.ui.state.ProjectHistoryEntryUi
import com.meadow.feature.project.ui.state.ProjectHistorySessionUi
import com.meadow.feature.project.ui.state.ProjectHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.abs


/* ─────────────────────────────────────────────
 * Project History ViewModel
 *
 * Responsibilities:
 * - Observe project + history changes
 * - Group changes into sessions
 * - Resolve human-readable field labels
 * - Expose UI-ready history state
 * ───────────────────────────────────────────── */

@HiltViewModel
class ProjectHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val historyRepo: ProjectFieldHistoryContract,
    private val projectRepo: ProjectRepositoryContract,
    private val templateRegistry: ProjectTemplateRegistry
) : ViewModel() {
    /* ─── PROJECT ID ───────────────────────── */
    private val projectId: String =
        requireNotNull(savedStateHandle["projectId"]) { "projectId is required for ProjectHistoryViewModel" }

    /* ─── UI STATE ────────────────────── */
    val uiState: StateFlow<ProjectHistoryUiState> =
        projectRepo.observeProject(projectId)
            .filterNotNull()
            .flatMapLatest { project ->
                val template = templateRegistry.getTemplate(project.type.key)

                historyRepo.observeProjectHistory(projectId)
                    .map { entries ->
                        val sessions: List<ProjectHistorySessionUi> =
                            entries
                                .sortedByDescending { it.changedAt }
                                .groupBySession()
                                .map { session ->
                                    val byField = session.groupBy { it.fieldId }
                                    ProjectHistorySessionUi(
                                        timestamp = session.first().changedAt,
                                        fields = byField.entries.map { (fieldId, fieldEntries) ->
                                            ProjectFieldHistoryGroupUi(
                                                fieldId = fieldId,
                                                label = resolveFieldLabel(fieldId, template),
                                                changes = fieldEntries.map { e ->
                                                    ProjectHistoryEntryUi(
                                                        fieldId = e.fieldId,
                                                        fieldLabel = resolveFieldLabel(e.fieldId, template),
                                                        oldValue = e.oldValue,
                                                        newValue = e.newValue,
                                                        changedAt = e.changedAt,
                                                        source = e.source,
                                                        isSeriesField = false
                                                    )
                                                }
                                            )
                                        }
                                    )
                                }

                        ProjectHistoryUiState(
                            isLoading = false,
                            sessions = sessions,
                            error = null
                        )
                    }
                    .onStart { emit(ProjectHistoryUiState(isLoading = true)) }
                    .catch { e -> emit(ProjectHistoryUiState(isLoading = false, error = e.message)) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProjectHistoryUiState())
    /* ─── FIELD LABEL RESOLUTION ───────────── */
    private fun resolveFieldLabel(
        fieldId: String,
        template: com.meadow.feature.project.domain.model.ProjectTemplate?
    ): String {
        val def = template?.fields?.firstOrNull { it.id == fieldId }
        return when {
            def?.key?.isNotBlank() == true -> humanize(def.key)
            def?.id?.isNotBlank() == true -> humanize(def.id)
            else -> humanize(fieldId)
        }
    }
    private fun humanize(id: String): String =
        id.substringAfterLast(".")
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }

    /* ─── SESSION GROUPING LOGIC ───────────── */
    private fun List<ProjectFieldHistory>.groupBySession(thresholdMs: Long = 60_000): List<List<ProjectFieldHistory>> {
        if (isEmpty()) return emptyList()
        val groups = mutableListOf<MutableList<ProjectFieldHistory>>()
        var current = mutableListOf<ProjectFieldHistory>()

        forEach { entry ->
            if (current.isEmpty()) {
                current.add(entry)
            } else {
                val last = current.last()
                val isRecent = abs(last.changedAt - entry.changedAt) < thresholdMs
                val isSameSource = last.source == entry.source
                if (isRecent && isSameSource) current.add(entry)
                else {
                    groups.add(current)
                    current = mutableListOf(entry)
                }
            }
        }
        if (current.isNotEmpty()) groups.add(current)
        return groups
    }
}
