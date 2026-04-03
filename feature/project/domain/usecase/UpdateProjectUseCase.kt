package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract

class UpdateProjectUseCase(
    private val repo: ProjectRepositoryContract
) {
    suspend operator fun invoke(project: Project) {
        repo.updateProject(project)
    }
}
