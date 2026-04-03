package com.meadow.core.database.history

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    indices = [
        Index(value = ["ownerId", "ownerType"]),
        Index(value = ["timestamp"]),
        Index(value = ["sessionId"]),
        Index(value = ["fieldId"]),
        Index(value = ["parentFieldId"])
    ]
)
data class HistoryEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val ownerType: String,
    val fieldId: String,
    val oldValue: String?,
    val newValue: String?,
    val parentFieldId: String?,
    val source: String,
    val timestamp: Long,
    val sessionId: String
)