package com.meadow.core.sync.api

sealed class SyncResult {
    data object Idle : SyncResult()
    data object InProgress : SyncResult()
    data class Success(val message: String = "Sync Completed") : SyncResult()
    data class Error(val exception: Throwable) : SyncResult()
}
