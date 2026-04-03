package com.meadow.feature.project.ui.state

import com.meadow.feature.project.domain.model.ProjectHistoryItem
import com.meadow.feature.project.domain.model.ChangeSource

data class ProjectHistoryUiState(
    val isLoading: Boolean = true,
    val sessions: List<ProjectHistorySessionUi> = emptyList(),
    val error: String? = null
)

data class ProjectHistorySessionUi(
    val timestamp: Long,
    val fields: List<ProjectFieldHistoryGroupUi>
)

data class ProjectFieldHistoryGroupUi(
    val fieldId: String,
    val label: String,
    val changes: List<ProjectHistoryEntryUi>
)

data class ProjectHistoryEntryUi(
    val fieldId: String,
    val fieldLabel: String,
    val oldValue: String?,
    val newValue: String?,
    val changedAt: Long,
    val source: ChangeSource,
    val isSeriesField: Boolean
)
