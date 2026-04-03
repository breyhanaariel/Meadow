package com.meadow.core.data.repository

import com.meadow.core.domain.repository.SyncRepositoryContract
import com.meadow.core.domain.sync.SyncCoordinator
import com.meadow.core.common.functional.ResultX
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val coordinator: SyncCoordinator
) : SyncRepositoryContract {

    override suspend fun syncAll(): ResultX<Unit> =
        coordinator.syncAll().fold(
            onSuccess = { ResultX.ok(Unit) },
            onFailure = { ResultX.err(it.message ?: "Sync failed", cause = it) }
        )

    override suspend fun syncProject(projectId: String): ResultX<Unit> =
        coordinator.syncProject(projectId).fold(
            onSuccess = { ResultX.ok(Unit) },
            onFailure = { ResultX.err(it.message ?: "Project sync failed", cause = it) }
        )
}
