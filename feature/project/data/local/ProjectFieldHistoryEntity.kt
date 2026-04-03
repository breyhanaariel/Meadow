package com.meadow.feature.project.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "project_field_history",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["projectId", "field"])
    ]
)
data class ProjectFieldHistoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val field: String,
    val oldValue: String?,
    val newValue: String?,
    val changedAt: Long
)
