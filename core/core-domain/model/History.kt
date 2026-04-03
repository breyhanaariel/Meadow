package com.meadow.core.domain.model

data class HistoryEntry(
    val id: String,
    val ownerId: String,
    val ownerType: HistoryOwnerType,
    val fieldId: String,
    val oldValue: String?,
    val newValue: String?,
    val parentFieldId: String?,
    val source: HistorySource,
    val timestamp: Long,
    val sessionId: String
)

enum class HistoryOwnerType {
    PROJECT,
    SERIES,
    CATALOG,
    SCRIPT,
    NODE,
    CALENDAR,
    TIMELINE
}

enum class HistorySource {
    MANUAL,
    SYNC,
    MIGRATION,
    SYSTEM
}