package com.meadow.feature.project.ui.state

enum class ProjectSyncStatus {
    IDLE,
    SYNCING,
    ERROR
}

data class ProjectSyncUiState(
    val status: ProjectSyncStatus = ProjectSyncStatus.IDLE,
    val lastSyncedAt: Long? = null,
    val errorMessage: String? = null
) {
    val isSyncing: Boolean get() = status == ProjectSyncStatus.SYNCING
    val isError: Boolean get() = status == ProjectSyncStatus.ERROR
}