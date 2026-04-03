package com.meadow.feature.project.domain.usecase

import javax.inject.Inject
import com.meadow.feature.project.domain.repository.ProjectHistoryContract

class RestoreProjectUseCase @Inject constructor(
    private val repo: ProjectHistoryContract
) {
    suspend operator fun invoke(historyId: String): Boolean =
        repo.restoreProjectFromHistory(historyId) != null
}