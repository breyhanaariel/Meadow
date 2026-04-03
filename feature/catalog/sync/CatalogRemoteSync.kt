package com.meadow.feature.catalog.sync

import com.meadow.core.data.firebase.FirestoreHelper
import com.meadow.core.sync.api.RemoteSyncContract
import com.meadow.core.sync.api.SyncableDataSource
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogSyncMeta
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import com.meadow.feature.catalog.sync.firestore.CatalogFirestoreMapper
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRemoteSync @Inject constructor(
    private val firestore: FirestoreHelper,
    private val mapper: CatalogFirestoreMapper,
    private val repo: CatalogRepositoryContract
) : RemoteSyncContract, SyncableDataSource<CatalogItem> {

    override suspend fun sync(): Boolean {
        val local = getLocalData()
        val remote = getRemoteData()
        val merged = merge(local, remote)
        pushLocalData(merged)
        saveRemoteData(merged)
        return true
    }

    private val now: Long get() = System.currentTimeMillis()

    private fun catalogCollection() =
        firestore.firestore.collection("catalog_items")

    /* ─── LOAD  ───────────────────── */

    override suspend fun getLocalData(): List<CatalogItem> =
        repo.getAllCatalogsOnce()

    override suspend fun getRemoteData(): List<CatalogItem> {
        val snapshot = catalogCollection().get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            mapper.fromMap(doc.id, data)
        }
    }

    /* ─── MERGE ───────────────────── */

    override suspend fun merge(
        localData: List<CatalogItem>,
        remoteData: List<CatalogItem>
    ): List<CatalogItem> {

        val localById = localData.associateBy { it.id }
        val remoteById = remoteData.associateBy { it.id }

        val allIds = localById.keys + remoteById.keys
        val merged = ArrayList<CatalogItem>(allIds.size)

        for (id in allIds) {
            val local = localById[id]
            val remote = remoteById[id]

            val resolved = resolveLastWriteWins(local, remote)
            if (resolved != null) merged.add(resolved)
        }

        return merged
    }

    private fun resolveLastWriteWins(
        local: CatalogItem?,
        remote: CatalogItem?
    ): CatalogItem? {

        if (local == null) return remote
        if (remote == null) return local

        val localMeta = local.syncMeta
        val remoteMeta = remote.syncMeta

        val localTs =
            local.updatedAt.takeIf { it > 0 }
                ?: localMeta.localUpdatedAt
                ?: 0L

        val remoteTs =
            remote.updatedAt.takeIf { it > 0 }
                ?: remoteMeta.remoteUpdatedAt
                ?: 0L

        return when {
            localTs > remoteTs -> {
                local.copy(
                    syncMeta = localMeta.copy(
                        remoteVersion =
                            maxOf(localMeta.remoteVersion, remoteMeta.remoteVersion),
                        remoteUpdatedAt = localTs,
                        isDirty = false,
                        hasConflict = false
                    )
                )
            }

            remoteTs > localTs -> {
                remote.copy(
                    syncMeta = CatalogSyncMeta(
                        localVersion =
                            maxOf(localMeta.localVersion, remoteMeta.remoteVersion),
                        remoteVersion = remoteMeta.remoteVersion,
                        localUpdatedAt = remoteTs,
                        remoteUpdatedAt = remoteTs,
                        isDirty = false,
                        hasConflict = false
                    )
                )
            }

            else -> {
                if (localMeta.localVersion >= remoteMeta.remoteVersion) {
                    local.copy(
                        syncMeta = localMeta.copy(
                            remoteVersion = localMeta.localVersion,
                            remoteUpdatedAt = localTs,
                            isDirty = false
                        )
                    )
                } else {
                    remote.copy(
                        syncMeta = CatalogSyncMeta(
                            localVersion = remoteMeta.remoteVersion,
                            remoteVersion = remoteMeta.remoteVersion,
                            localUpdatedAt = remoteTs,
                            remoteUpdatedAt = remoteTs,
                            isDirty = false
                        )
                    )
                }
            }
        }
    }

    /* ─── APPLY MERGED TO LOCAL ───────────────────── */

    override suspend fun pushLocalData(data: List<CatalogItem>) {
        repo.replaceAllCatalogItems(data)

        data.forEach { item ->
            val meta = item.syncMeta
            repo.updateCatalogSyncMeta(
                item.id,
                meta.copy(
                    isDirty = false,
                    hasConflict = false,
                    lastFirestoreSyncAt = now,
                    lastSyncError = null,
                    retryCount = 0,
                    remoteVersion =
                        maxOf(meta.remoteVersion, meta.localVersion),
                    remoteUpdatedAt =
                        meta.remoteUpdatedAt ?: item.updatedAt
                )
            )
        }
    }

    /* ─── SAVE TO FIRESTORE ───────────────────── */

    override suspend fun saveRemoteData(data: List<CatalogItem>) {
        val remoteSnapshot =
            catalogCollection().get().await().documents.associateBy { it.id }

        val batch = firestore.firestore.batch()
        var writes = 0

        data.forEach { merged ->
            val remoteDoc = remoteSnapshot[merged.id]
            val remoteItem =
                remoteDoc?.data?.let { mapper.fromMap(merged.id, it) }

            if (shouldWriteRemote(merged, remoteItem)) {
                val docRef = catalogCollection().document(merged.id)
                batch.set(docRef, mapper.toMap(merged))
                writes++
            }
        }

        if (writes > 0) {
            batch.commit().await()
        }
    }

    private fun shouldWriteRemote(
        merged: CatalogItem,
        remoteExisting: CatalogItem?
    ): Boolean {

        if (remoteExisting == null) return true

        val mergedTs =
            merged.updatedAt.takeIf { it > 0 }
                ?: merged.syncMeta.localUpdatedAt
                ?: 0L

        val remoteTs =
            remoteExisting.updatedAt.takeIf { it > 0 }
                ?: remoteExisting.syncMeta.remoteUpdatedAt
                ?: 0L

        return mergedTs > remoteTs
    }
}
