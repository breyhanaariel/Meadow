package com.meadow.core.data.sync

import com.meadow.core.domain.repository.SyncRepositoryContract
import com.meadow.core.domain.sync.SyncCoordinator
import com.meadow.core.domain.sync.SyncState
import com.meadow.core.data.firebase.FirebaseSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSyncCoordinator @Inject constructor(
    private val firebaseSyncManager: FirebaseSyncManager
) : SyncCoordinator {

    private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
    override val state: StateFlow<SyncState> = _state

    override suspend fun startAll(syncRepo: SyncRepositoryContract) {
        _state.value = SyncState.Running

        val result = syncAll()

        _state.value =
            if (result.isSuccess) SyncState.Success
            else SyncState.Error(result.exceptionOrNull())
    }

    override suspend fun startProject(
        syncRepo: SyncRepositoryContract,
        projectId: String
    ) {
        _state.value = SyncState.Running

        val result = syncProject(projectId)

        _state.value =
            if (result.isSuccess) SyncState.Success
            else SyncState.Error(result.exceptionOrNull())
    }

    override suspend fun syncAll(): Result<Unit> =
        firebaseSyncManager.syncStorageOnly()

    override suspend fun syncProject(projectId: String): Result<Unit> =
        Result.success(Unit)
}
