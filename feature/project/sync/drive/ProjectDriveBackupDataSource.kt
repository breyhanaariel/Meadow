package com.meadow.feature.project.sync.drive

import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectDriveBackupDataSource @Inject constructor(
    private val repo: ProjectRepositoryContract,
    private val serializer: ProjectDriveSerializer
) {

    suspend fun exportProject(projectId: String): Pair<Project, String> {
        val project = repo.getProjectById(projectId)
            ?: throw IllegalArgumentException("Project not found")

        return project to serializer.serialize(project)
    }

    suspend fun exportAllProjects(): List<Pair<Project, String>> {
        return repo.getAllProjectsOnce().map { project ->
            project to serializer.serialize(project)
        }
    }

    suspend fun import(json: String) {
        val projects = serializer.deserialize(json)
        repo.replaceAllProjects(projects)
    }
}
