package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import jakarta.inject.Inject

class RevertSeriesToProjectUseCase @Inject constructor(
    private val projectRepo: ProjectRepositoryContract
) {
    suspend operator fun invoke(projectId: String): Boolean {
        val project = projectRepo.getProjectById(projectId)
            ?: error("Project not found")

        if (project.seriesId == null) return false

        projectRepo.updateProject(
            project.copy(seriesId = null)
        )

        return true
    }
}
