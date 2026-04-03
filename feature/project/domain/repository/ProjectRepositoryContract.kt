package com.meadow.feature.project.domain.repository

import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectSyncMeta
import kotlinx.coroutines.flow.Flow

interface ProjectRepositoryContract {

    fun observeAllProjects(): Flow<List<Project>>
    fun observeProjects(): Flow<List<Project>>
    fun observeProject(id: String): Flow<Project?>

    suspend fun getProjectById(id: String): Project?
    suspend fun getAllProjectsOnce(): List<Project>

    suspend fun createProject(project: Project)
    suspend fun updateProject(project: Project)

    suspend fun deleteProject(id: String)

    suspend fun replaceAllProjects(projects: List<Project>)

    suspend fun syncProject(projectId: String)
    suspend fun syncAllProjects()

    suspend fun updateProjectSyncMeta(
        projectId: String,
        meta: ProjectSyncMeta
    )
    suspend fun updateProjectField(
        projectId: String,
        fieldKey: String,
        value: String?
    )

    suspend fun updateProjectSeries(
        projectId: String,
        seriesId: String?
    )

}
