package com.meadow.app.ui.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.feature.project.api.ProjectSelectorItem
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainShellViewModel @Inject constructor(
    private val projectRepository: ProjectRepositoryContract
) : ViewModel() {

    /* ─── PROJECTS ───────────────────── */

    val projects: StateFlow<List<Project>> =
        projectRepository.observeProjects()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val projectCount: StateFlow<Int> =
        projects
            .map { it.size }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

    val hasProjects: StateFlow<Boolean> =
        projects
            .map { it.isNotEmpty() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    /* ─── SELECTED PROJECT ID ───────────────────── */

    private val _selectedProjectId = MutableStateFlow<String?>(null)
    val selectedProjectId: StateFlow<String?> =
        _selectedProjectId.asStateFlow()

    /* ─── PROJECT PICKER ITEMS ───────────────────── */

    val projectPickerItems: StateFlow<List<ProjectSelectorItem>> =
        projects
            .map { list ->
                list.map { project ->
                    ProjectSelectorItem(
                        id = project.id,
                        title = "",
                        typeKey = project.type.name
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    /* ─── PROJECT SELECTION ───────────────────── */

    fun selectProject(projectId: String) {
        _selectedProjectId.value = projectId
    }

    fun selectedProject(projectId: String?): StateFlow<Project?> =
        projects
            .map { list ->
                list.firstOrNull { it.id == projectId }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )
}
