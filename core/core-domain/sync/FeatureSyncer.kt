package com.meadow.core.domain.sync

interface FeatureSyncer {
    suspend fun syncAll(): Result<Unit>
    suspend fun syncProject(projectId: String): Result<Unit>
}
