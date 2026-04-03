package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectHistoryContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectHistoryUseCase @Inject constructor(
    private val historyRepo: ProjectHistoryContract
) {
    fun execute(projectId: String) =
        historyRepo.observeProjectHistory(projectId)
}