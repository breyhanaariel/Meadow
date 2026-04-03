package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import jakarta.inject.Inject

class RemoveProjectFromSeriesUseCase @Inject constructor(
    private val projectRepo: ProjectRepositoryContract
) {

    suspend operator fun invoke(projectId: String) {
        val project = projectRepo.getProjectById(projectId)
            ?: error("Project not found")

        if (project.seriesId == null) return

        projectRepo.updateProject(
            project.copy(seriesId = null)
        )
    }
}
