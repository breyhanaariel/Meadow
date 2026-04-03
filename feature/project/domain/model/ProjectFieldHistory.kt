package com.meadow.feature.project.domain.model

data class ProjectFieldHistory(
    val projectId: String,
    val fieldId: String,
    val oldValue: String?,
    val newValue: String?,
    val changedAt: Long,
    val source: ChangeSource,
    val isSeriesField: Boolean
)

enum class ChangeSource {
    MANUAL,
    SYNC,
    MIGRATION
}
