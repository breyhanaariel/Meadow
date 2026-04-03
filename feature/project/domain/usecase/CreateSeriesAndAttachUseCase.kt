package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import jakarta.inject.Inject

class CreateSeriesAndAttachUseCase @Inject constructor(
    private val projectRepo: ProjectRepositoryContract,
    private val seriesRepo: SeriesRepositoryContract
) {
    suspend operator fun invoke(projectId: String, title: String): String {
        val seriesId = seriesRepo.createSeries(title)
        projectRepo.updateProject(
            projectRepo.getProjectById(projectId)!!.copy(seriesId = seriesId)
        )
        return seriesId
    }
}
