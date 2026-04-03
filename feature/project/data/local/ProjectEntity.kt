package com.meadow.feature.project.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.feature.project.domain.model.ProjectSyncMeta
import com.meadow.feature.project.domain.model.ProjectType
import java.util.UUID
import com.meadow.feature.project.ui.state.ProjectSyncStatus

@Entity(
    tableName = "projects",
    indices = [
        Index(value = ["seriesId"]),
        Index(value = ["type"]),
        Index(value = ["updatedAt"]),
        Index(value = ["isDirty"])
    ]
)

data class ProjectEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val seriesId: String? = null,
    val type: String,
    val localVersion: Long = 0L,
    val remoteVersion: Long = 0L,
    val localUpdatedAt: Long? = null,
    val remoteUpdatedAt: Long? = null,
    val lastFirestoreSyncAt: Long? = null,
    val lastDriveBackupAt: Long? = null,
    val isDirty: Int = 0,
    val hasConflict: Int = 0,
    val lastSyncError: String? = null,
    val retryCount: Int = 0,
    val updatedAt: Long? = null,
    val startDate: Long? = null,
    val finishDate: Long? = null
)
