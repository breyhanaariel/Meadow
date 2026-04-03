package com.meadow.core.domain.sync

sealed class SyncState {
    object Idle : SyncState()
    object Pending : SyncState()
    object Running : SyncState()
    data class Failed(val message: String) : SyncState()
    object Completed : SyncState()
    data object Success : SyncState()
    data class Error(val throwable: Throwable?) : SyncState()
}