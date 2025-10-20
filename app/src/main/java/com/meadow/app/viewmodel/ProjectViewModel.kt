package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repository.ProjectRepository
import com.meadow.app.data.room.entities.ProjectEntity
import com.meadow.app.sync.WorkManagerScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ProjectViewModel.kt
 *
 * Handles project CRUD and sync triggers.
 */
@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepo: ProjectRepository,
    private val syncScheduler: WorkManagerScheduler
) : ViewModel() {

    private val _projects = MutableStateFlow<List<ProjectEntity>>(emptyList())
    val projects: StateFlow<List<ProjectEntity>> = _projects.asStateFlow()

    private val _selectedProject = MutableStateFlow<ProjectEntity?>(null)
    val selectedProject = _selectedProject.asStateFlow()

    init {
        // Observe all projects in database
        viewModelScope.launch {
            projectRepo.getAllProjects().collect {
                _projects.value = it
            }
        }
    }

    fun selectProject(project: ProjectEntity) {
        _selectedProject.value = project
    }

    fun createProject(title: String, type: String) {
        viewModelScope.launch {
            val newProject = ProjectEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                type = type
            )
            projectRepo.saveProject(newProject)
            _selectedProject.value = newProject
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectRepo.deleteProject(project)
        }
    }

    fun syncProjectNow(projectId: String) {
        syncScheduler.scheduleSync(projectId, immediate = true)
    }
}