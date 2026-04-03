package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProjectDashboardUseCase @Inject constructor(
    private val repo: ProjectRepositoryContract
) {
    operator fun invoke(): Flow<List<Project>> =
        repo.observeAllProjects()
}
