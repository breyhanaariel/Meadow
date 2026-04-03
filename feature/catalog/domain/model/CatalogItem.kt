package com.meadow.feature.catalog.domain.model
import com.meadow.core.data.fields.FieldWithValue

data class CatalogItem(
    val id: String,
    val schemaId: String,
    val projectId: String?,
    val seriesId: String?,
    val fields: List<FieldWithValue> = emptyList(),
    val primaryText: String? = null,
    val searchBlob: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val syncMeta: CatalogSyncMeta
) {
    init {
        require(projectId != null || seriesId != null) {
            "CatalogItem must have either projectId or seriesId"
        }
    }
}

data class CatalogSyncMeta(
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

