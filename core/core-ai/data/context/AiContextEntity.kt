package com.meadow.core.ai.data.context

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_contexts",
    indices = [
        Index(value = ["scopeKey"]),
        Index(value = ["enabled"]),
        Index(value = ["pinned"]),
        Index(value = ["personaKeysCsv"]),
        Index(value = ["personaKeysCsv", "scopeKey"])
    ]
)
data class AiContextEntity(
    @PrimaryKey val id: String,
    val title: String,
    val text: String,
    val category: String,
    val scopeKey: String?,
    val personaKeysCsv: String?,
    val enabled: Boolean,
    val pinned: Boolean,
    val createdAtUtcMs: Long,
    val updatedAtUtcMs: Long
)
