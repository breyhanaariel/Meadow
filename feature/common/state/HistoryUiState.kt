package com.meadow.feature.common.state

data class HistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessions: List<HistorySessionUi> = emptyList(),
    val isRestoring: Boolean = false
)

data class HistorySessionUi(
    val sessionId: String,
    val timestamp: Long,
    val entries: List<HistoryEntryUi>
)

data class HistoryEntryUi(
    val id: String,
    val fieldId: String,
    val label: String,
    val oldValue: String?,
    val newValue: String?,
    val timestamp: Long,
    val canRestore: Boolean
)