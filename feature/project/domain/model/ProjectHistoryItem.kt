package com.meadow.feature.project.domain.model

data class ProjectHistoryItem(
    val id: String,
    val projectId: String,
    val action: ProjectHistoryAction,
    val timestamp: Long,
    val summary: String?
)

enum class ProjectHistoryAction {
    CREATED,
    UPDATED,
    DELETED,
    RESTORED
}