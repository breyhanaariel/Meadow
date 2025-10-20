package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repository.ProjectRepository
import com.meadow.app.data.room.entities.ProjectEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel.kt
 *
 * Handles all logic for the Home screen.
 * Loads, filters, and deletes projects.
 */

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectRepo: ProjectRepository
) : ViewModel() {

    // Reactive list of all projects
    val projects = projectRepo.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectRepo.deleteProject(project)
        }
    }

    fun addProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectRepo.saveProject(project)
        }
    }

    fun searchProjects(query: String): List<ProjectEntity> {
        return projects.value.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }
}
