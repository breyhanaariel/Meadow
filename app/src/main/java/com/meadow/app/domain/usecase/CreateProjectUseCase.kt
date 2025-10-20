package com.meadow.app.domain.usecase

import com.meadow.app.data.repository.ProjectRepository
import com.meadow.app.data.room.entities.ProjectEntity
import javax.inject.Inject

/**
 * CreateProjectUseCase.kt
 *
 * Handles creating and saving new projects.
 * Keeps UI logic separate from persistence.
 */

class CreateProjectUseCase @Inject constructor(
    private val repo: ProjectRepository
) {
    suspend operator fun invoke(project: ProjectEntity) {
        repo.saveProject(project)
    }
}
