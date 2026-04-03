package com.meadow.core.sync.api


interface RemoteSyncContract {
    suspend fun sync(): Boolean
}
