package com.meadow.core.sync.engine

import com.meadow.core.sync.api.RemoteSyncContract
import com.meadow.core.sync.api.SyncableDataSource

class SyncEngine<T>(
    private val dataSource: SyncableDataSource<T>,
    private val serializer: SyncSerializer<T>,
    private val hasher: SyncHasher<T>,
    private val conflictResolver: SyncConflictResolver<T>
) : RemoteSyncContract {

    override suspend fun sync(): Boolean {
        val local = dataSource.getLocalData()
        val remote = dataSource.getRemoteData()

        val merged = merge(local, remote)

        dataSource.saveRemoteData(merged)
        dataSource.pushLocalData(merged)

        return true
    }

    private fun merge(local: List<T>, remote: List<T>): List<T> {
        if (local.isEmpty()) return remote
        if (remote.isEmpty()) return local

        val merged = mutableListOf<T>()

        val maxSize = maxOf(local.size, remote.size)
        for (i in 0 until maxSize) {
            val l = local.getOrNull(i)
            val r = remote.getOrNull(i)

            when {
                l != null && r != null -> merged += conflictResolver.resolve(l, r)
                l != null -> merged += l
                r != null -> merged += r
            }
        }

        return merged
    }
}
