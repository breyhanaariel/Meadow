package com.meadow.feature.project.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "project_field_values")
data class ProjectFieldValueEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val fieldId: String,
    val rawValue: String?,
    val updatedAt: Long
)
