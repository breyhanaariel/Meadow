package com.meadow.feature.catalog.sync.firestore

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogSyncMeta

class CatalogFirestoreMapper {

    fun fromMap(
        id: String,
        data: Map<String, Any?>
    ): CatalogItem? {

        val schemaId = data["schemaId"] as? String ?: return null
        val createdAt = (data["createdAt"] as? Number)?.toLong() ?: return null
        val updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: return null

        val projectId = data["projectId"] as? String
        val seriesId = data["seriesId"] as? String

        // Enforce domain invariant safely
        if (projectId == null && seriesId == null) return null

        val sync = data["sync"] as? Map<*, *>

        val syncMeta = CatalogSyncMeta(
            localVersion = (sync?.get("localVersion") as? Number)?.toLong() ?: 0L,
            remoteVersion = (sync?.get("remoteVersion") as? Number)?.toLong() ?: 0L,
            localUpdatedAt = (sync?.get("localUpdatedAt") as? Number)?.toLong(),
            remoteUpdatedAt = (sync?.get("remoteUpdatedAt") as? Number)?.toLong(),
            lastFirestoreSyncAt = (sync?.get("lastFirestoreSyncAt") as? Number)?.toLong(),
            lastDriveBackupAt = (sync?.get("lastDriveBackupAt") as? Number)?.toLong(),
            isDirty = sync?.get("isDirty") as? Boolean ?: false,
            hasConflict = sync?.get("hasConflict") as? Boolean ?: false,
            lastSyncError = sync?.get("lastSyncError") as? String,
            retryCount = (sync?.get("retryCount") as? Number)?.toInt() ?: 0
        )

        return CatalogItem(
            id = id,
            schemaId = schemaId,
            projectId = projectId,
            seriesId = seriesId,
            fields = emptyList(), // Intentionally NOT synced via Firestore
            primaryText = data["primaryText"] as? String,
            searchBlob = data["searchBlob"] as? String,
            createdAt = createdAt,
            updatedAt = updatedAt,
            syncMeta = syncMeta
        )
    }

    fun toMap(item: CatalogItem): Map<String, Any?> =
        mapOf(
            "schemaId" to item.schemaId,
            "projectId" to item.projectId,
            "seriesId" to item.seriesId,
            "primaryText" to item.primaryText,
            "searchBlob" to item.searchBlob,
            "createdAt" to item.createdAt,
            "updatedAt" to item.updatedAt,
            "sync" to mapOf(
                "localVersion" to item.syncMeta.localVersion,
                "remoteVersion" to item.syncMeta.remoteVersion,
                "localUpdatedAt" to item.syncMeta.localUpdatedAt,
                "remoteUpdatedAt" to item.syncMeta.remoteUpdatedAt,
                "lastFirestoreSyncAt" to item.syncMeta.lastFirestoreSyncAt,
                "lastDriveBackupAt" to item.syncMeta.lastDriveBackupAt,
                "isDirty" to item.syncMeta.isDirty,
                "hasConflict" to item.syncMeta.hasConflict,
                "lastSyncError" to item.syncMeta.lastSyncError,
                "retryCount" to item.syncMeta.retryCount
            )
        )
}
