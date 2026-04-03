package com.meadow.feature.project.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_history",
    indices = [
        Index("projectId"),
        Index("timestamp")
    ]
)
data class ProjectHistoryEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val action: String,
    val timestamp: Long,
    val snapshotJson: String,
    val summary: String?
)