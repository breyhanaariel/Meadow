package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectRepositoryContract

import jakarta.inject.Inject

class AddProjectToSeriesUseCase @Inject constructor(
    private val projectRepo: ProjectRepositoryContract
) {

    suspend operator fun invoke(
        projectId: String,
        seriesId: String
    ) {
        val project = projectRepo.getProjectById(projectId)
            ?: error("Project not found")

        if (project.seriesId == seriesId) return

        projectRepo.updateProject(
            project.copy(seriesId = seriesId)
        )
    }
}
