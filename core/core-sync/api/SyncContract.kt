package com.meadow.core.sync.api

import kotlinx.coroutines.flow.Flow

interface SyncContract {
    suspend fun performFullSync(): SyncResult
    suspend fun performDeltaSync(): SyncResult
    fun observeSyncProgress(): Flow<SyncResult>
}
