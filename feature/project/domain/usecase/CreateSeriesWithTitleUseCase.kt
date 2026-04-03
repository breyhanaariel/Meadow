package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import jakarta.inject.Inject

class CreateSeriesWithTitleUseCase @Inject constructor(
    private val projectRepo: ProjectRepositoryContract,
    private val seriesRepo: SeriesRepositoryContract
) {
    suspend operator fun invoke(projectId: String, title: String): String {
        val seriesId = seriesRepo.createSeries(title)
        projectRepo.updateProjectSeries(projectId, seriesId)
        return seriesId
    }
}
