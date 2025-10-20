package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.ProjectDao
import com.meadow.app.data.room.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ProjectRepository.kt
 *
 * Handles CRUD operations for projects.
 * Acts as the interface between the local database and Firestore sync.
 */

@Singleton
class ProjectRepository @Inject constructor(
    private val dao: ProjectDao
) {

    /** Returns all stored projects as a Flow (reactive list). */
    fun getAllProjects(): Flow<List<ProjectEntity>> = dao.getAllProjects()

    /** Returns one project by ID. */
    suspend fun getProjectById(id: String): ProjectEntity? = dao.getProject(id)

    /** Saves a new or existing project. */
    suspend fun saveProject(project: ProjectEntity) = dao.insert(project)

    /** Deletes a project. */
    suspend fun deleteProject(project: ProjectEntity) = dao.delete(project)
}
