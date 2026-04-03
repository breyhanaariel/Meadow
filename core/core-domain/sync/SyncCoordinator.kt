package com.meadow.core.domain.sync

import com.meadow.core.domain.repository.SyncRepositoryContract
import com.meadow.core.domain.sync.SyncState
import kotlinx.coroutines.flow.StateFlow

interface SyncCoordinator {
    val state: StateFlow<SyncState>
    suspend fun startAll(syncRepo: SyncRepositoryContract)
    suspend fun startProject(syncRepo: SyncRepositoryContract, projectId: String)
    suspend fun syncAll(): Result<Unit>
    suspend fun syncProject(projectId: String): Result<Unit>
}