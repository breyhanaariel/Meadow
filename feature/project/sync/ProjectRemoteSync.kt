package com.meadow.feature.project.sync

import com.meadow.core.data.firebase.FirestoreHelper
import com.meadow.core.sync.api.SyncableDataSource
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectSyncMeta
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.sync.firestore.ProjectFirestoreMapper
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.meadow.core.sync.api.RemoteSyncContract

@Singleton
class ProjectRemoteSync @Inject constructor(
    private val firestore: FirestoreHelper,
    private val mapper: ProjectFirestoreMapper,
    private val repo: ProjectRepositoryContract
) : RemoteSyncContract, SyncableDataSource<Project> {

    override suspend fun sync(): Boolean {
        val local = getLocalData()
        val remote = getRemoteData()
        val merged = merge(local, remote)
        pushLocalData(merged)
        saveRemoteData(merged)
        return true
    }

    private val now: Long get() = System.currentTimeMillis()

    private fun projectsCollection() =
        firestore.firestore.collection("projects")

    override suspend fun getLocalData(): List<Project> =
        repo.getAllProjectsOnce()

    override suspend fun getRemoteData(): List<Project> {
        val snapshot = projectsCollection()
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            mapper.fromMap(doc.id, data)
        }
    }

    /**
     * merged = merge(local, remote)
     * pushLocalData(merged)
     * saveRemoteData(merged)
     */
    override suspend fun merge(
        localData: List<Project>,
        remoteData: List<Project>
    ): List<Project> {

        val localById = localData.associateBy { it.id }
        val remoteById = remoteData.associateBy { it.id }

        val allIds = (localById.keys + remoteById.keys)
        val merged = ArrayList<Project>(allIds.size)

        for (id in allIds) {
            val local = localById[id]
            val remote = remoteById[id]

            val resolved = resolveLastWriteWins(local, remote)
            if (resolved != null) merged.add(resolved)
        }

        return merged
    }

    /**
     * Apply merged results to local storage.
     */
    override suspend fun pushLocalData(data: List<Project>) {
        repo.replaceAllProjects(data)

        data.forEach { project ->
            val meta = project.syncMeta
            val cleaned = meta.copy(
                isDirty = false,
                hasConflict = false,
                lastFirestoreSyncAt = now,
                lastSyncError = null,
                retryCount = 0,
                remoteVersion = maxOf(meta.remoteVersion, meta.localVersion),
                remoteUpdatedAt = meta.remoteUpdatedAt
                    ?: project.updatedAt
                    ?: meta.localUpdatedAt
            )

            repo.updateProjectSyncMeta(project.id, cleaned)
        }
    }

    override suspend fun saveRemoteData(data: List<Project>) {
        val remoteSnapshot = projectsCollection()
            .get()
            .await()
            .documents
            .associateBy { it.id }

        val batch = firestore.firestore.batch()
        var writes = 0

        data.forEach { merged ->
            val remoteDoc = remoteSnapshot[merged.id]
            val remoteProject = remoteDoc?.data
                ?.let { mapper.fromMap(merged.id, it) }

            if (shouldWriteRemote(merged, remoteProject)) {
                val docRef = projectsCollection().document(merged.id)
                batch.set(docRef, mapper.toMap(merged))
                writes++
            }
        }

        if (writes > 0) {
            batch.commit().await()
        }
    }

    // ------------------------------------------------
    // LWW (Last Write Wins)
    // ------------------------------------------------

    private fun resolveLastWriteWins(
        local: Project?,
        remote: Project?
    ): Project? {

        if (local == null) return remote
        if (remote == null) return local

        val localMeta = local.syncMeta
        val remoteMeta = remote.syncMeta

        val localTs =
            local.updatedAt ?: localMeta.localUpdatedAt ?: 0L
        val remoteTs =
            remote.updatedAt ?: remoteMeta.remoteUpdatedAt ?: 0L

        return when {
            localTs > remoteTs -> {
                local.copy(
                    syncMeta = localMeta.copy(
                        remoteVersion =
                            maxOf(localMeta.remoteVersion, remoteMeta.remoteVersion),
                        remoteUpdatedAt = localTs
                    )
                )
            }

            remoteTs > localTs -> {
                remote.copy(
                    syncMeta = ProjectSyncMeta(
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
                val localV = localMeta.localVersion
                val remoteV = remoteMeta.remoteVersion

                if (localV >= remoteV) {
                    local.copy(
                        syncMeta = localMeta.copy(
                            remoteVersion = localV,
                            remoteUpdatedAt = localTs
                        )
                    )
                } else {
                    remote.copy(
                        syncMeta = ProjectSyncMeta(
                            localVersion = remoteV,
                            remoteVersion = remoteV,
                            localUpdatedAt = remoteTs,
                            remoteUpdatedAt = remoteTs,
                            isDirty = false,
                            hasConflict = false
                        )
                    )
                }
            }
        }
    }

    private fun shouldWriteRemote(
        merged: Project,
        remoteExisting: Project?
    ): Boolean {

        if (remoteExisting == null) return true

        val mergedTs =
            merged.updatedAt ?: merged.syncMeta.localUpdatedAt ?: 0L
        val remoteTs =
            remoteExisting.updatedAt
                ?: remoteExisting.syncMeta.remoteUpdatedAt
                ?: 0L

        return mergedTs > remoteTs
    }
}