package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import com.meadow.feature.project.ui.util.readTitleOrNull
import jakarta.inject.Inject



class ConvertToSeriesUseCase @Inject constructor(
    private val projectRepo: ProjectRepositoryContract,
    private val seriesRepo: SeriesRepositoryContract
) {

    suspend operator fun invoke(projectId: String): String {
        val project = projectRepo.getProjectById(projectId)
            ?: error("Project not found")

        val title = project.readTitleOrNull()
            ?: "Untitled"

        val seriesId = seriesRepo.createSeries(title)

        projectRepo.updateProject(
            project.copy(seriesId = seriesId)
        )

        return seriesId
    }
}
