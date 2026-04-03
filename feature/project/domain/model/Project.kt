package com.meadow.feature.project.domain.model

import com.meadow.core.data.fields.FieldWithValue

data class Project(
    val id: String,
    val seriesId: String?,
    val type: ProjectType,
    val syncMeta: ProjectSyncMeta = ProjectSyncMeta(),
    val updatedAt: Long?,
    val startDate: Long?,
    val finishDate: Long?,
    val fields: List<FieldWithValue>,
    val isSyncing: Boolean = false,
    val hasSyncError: Boolean = false
)

data class ProjectSyncMeta(
    val localVersion: Long = 0L,
    val remoteVersion: Long = 0L,
    val localUpdatedAt: Long? = null,
    val remoteUpdatedAt: Long? = null,
    val lastFirestoreSyncAt: Long? = null,
    val lastDriveBackupAt: Long? = null,
    val isDirty: Boolean = false,
    val hasConflict: Boolean = false,
    val lastSyncError: String? = null,
    val retryCount: Int = 0
)
