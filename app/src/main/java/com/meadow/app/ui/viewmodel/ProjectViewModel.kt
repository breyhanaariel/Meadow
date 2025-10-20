package com.meadow.app.ui.viewmodel

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repo: ProjectRepository,
    private val settings: SettingsDataStore
) : ViewModel() {

    val projects = repo.getAllProjects().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun selectProject(projectId: String) {
        viewModelScope.launch {
            settings.setLastProjectId(projectId)
        }
    }
}
