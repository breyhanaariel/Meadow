package com.meadow.core.data.sync

import com.meadow.core.domain.sync.FeatureSyncer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureSyncRegistry @Inject constructor() {

    private val syncers = mutableListOf<FeatureSyncer>()

    fun register(syncer: FeatureSyncer) {
        syncers += syncer
    }

    suspend fun syncAll(): Result<Unit> {
        syncers.forEach { it.syncAll() }
        return Result.success(Unit)
    }

    suspend fun syncProject(projectId: String): Result<Unit> {
        syncers.forEach { it.syncProject(projectId) }
        return Result.success(Unit)
    }
}
