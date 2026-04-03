package com.meadow.core.sync.local

import com.meadow.core.database.LocalRepositoryContract
import com.meadow.core.sync.api.SyncableDataSource

class LocalSyncDataSource<T>(
    private val localRepo: LocalRepositoryContract<T>
) : SyncableDataSource<T> {

    override suspend fun getLocalData(): List<T> =
        localRepo.getAll()

    override suspend fun getRemoteData(): List<T> {
        throw IllegalStateException("Local source cannot fetch remote data")
    }

    override suspend fun pushLocalData(data: List<T>) {
    }

    override suspend fun saveRemoteData(data: List<T>) {
        localRepo.replaceAll(data)
    }

    override suspend fun merge(
        localData: List<T>,
        remoteData: List<T>
    ): List<T> {
        return remoteData
    }
}
