package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.repository.ProjectRepositoryContract

class DeleteProjectUseCase(
    private val repo: ProjectRepositoryContract
) {
    suspend operator fun invoke(projectId: String) {
        repo.deleteProject(projectId)
    }
}
