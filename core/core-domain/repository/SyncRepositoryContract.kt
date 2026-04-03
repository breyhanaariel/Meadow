package com.meadow.core.domain.repository

import com.meadow.core.common.functional.ResultX

interface SyncRepositoryContract {
    suspend fun syncAll(): ResultX<Unit>
    suspend fun syncProject(projectId: String): ResultX<Unit>
}