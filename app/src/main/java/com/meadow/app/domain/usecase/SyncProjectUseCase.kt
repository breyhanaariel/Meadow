package com.meadow.app.domain.usecase

import com.meadow.app.data.repository.SyncRepository
import javax.inject.Inject

/**
 * SyncProjectUseCase.kt
 *
 * Performs manual sync for a given project.
 * Delegates to SyncRepository.
 */

class SyncProjectUseCase @Inject constructor(
    private val syncRepo: SyncRepository
) {
    suspend operator fun invoke(projectId: String) {
        syncRepo.syncProject(projectId)
    }
}
