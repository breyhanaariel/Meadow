package com.meadow.core.sync.api

interface SyncableDataSource<T> {

    suspend fun getLocalData(): List<T>

    suspend fun getRemoteData(): List<T>

    suspend fun pushLocalData(data: List<T>)

    suspend fun saveRemoteData(data: List<T>)

    suspend fun merge(
        localData: List<T>,
        remoteData: List<T>
    ): List<T>
}
