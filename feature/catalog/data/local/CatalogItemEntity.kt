package com.meadow.feature.catalog.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "catalog_items",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["seriesId"]),
        Index(value = ["schemaId"]),
        Index(value = ["updatedAt"]),
        Index(value = ["isDirty"])
    ]
)
data class CatalogItemEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val schemaId: String,
    val projectId: String?,
    val seriesId: String?,

    val primaryText: String?,
    val searchBlob: String?,

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

    val createdAt: Long,
    val updatedAt: Long
)
