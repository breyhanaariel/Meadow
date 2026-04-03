package com.meadow.feature.project.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.Series
import com.meadow.feature.project.domain.registry.ProjectTemplateRegistry
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import com.meadow.feature.project.ui.state.CreateProjectUiState
import com.meadow.feature.project.ui.state.EditProjectUiState
import com.meadow.feature.project.ui.state.ProjectFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ProjectEditorViewModel @Inject constructor(
    private val repo: ProjectRepositoryContract,
    private val templateRegistry: ProjectTemplateRegistry,
    private val seriesRepository: SeriesRepositoryContract,
    aiManager: AiManager
) : ProjectFormViewModel<ProjectFormState>(aiManager) {

    override val _uiState =
        MutableStateFlow<ProjectFormState>(CreateProjectUiState())

    private val _series = MutableStateFlow<List<Series>>(emptyList())
    val series: StateFlow<List<Series>> = _series

    private var loadedProject: Project? = null

    init {
        viewModelScope.launch {
            _series.value = seriesRepository.getAllSeriesOnce()
        }
    }

    fun load(projectId: String?) {

        if (projectId == null) {
            _uiState.value = CreateProjectUiState()
            return
        }

        viewModelScope.launch {

            val project = repo.getProjectById(projectId) ?: return@launch
            loadedProject = project

            val template =
                templateRegistry.getTemplate(project.type.key) ?: return@launch

            val existing =
                project.fields.associateBy { it.definition.id }

            val mergedFields =
                template.fields.map { def ->
                    existing[def.id]
                        ?: FieldWithValue(
                            definition = def,
                            value = FieldValue(
                                fieldId = def.id,
                                ownerItemId = project.id,
                                rawValue = null
                            )
                        )
                }

            _uiState.value =
                EditProjectUiState(
                    projectId = project.id,
                    projectType = project.type,
                    fields = mergedFields,
                    seriesId = project.seriesId,
                    seriesSharedFieldIds = emptySet(),
                    isLoading = false
                )
        }
    }

    fun updateFieldValue(value: FieldValue) =
        updateFieldInternal(value)

    fun updateSeries(seriesId: String?) =
        updateSeriesInternal(seriesId)

    fun toggleSeriesField(fieldId: String, makeSeries: Boolean) =
        toggleSeriesFieldInternal(fieldId, makeSeries)

    fun createSeriesInline(title: String) {

        viewModelScope.launch {

            val id = seriesRepository.createSeries(title)

            updateSeriesInternal(id)

            _series.value =
                seriesRepository.getAllSeriesOnce()
        }
    }

    fun renameSeries(seriesId: String, newTitle: String) {

        viewModelScope.launch {

            seriesRepository.renameSeries(seriesId, newTitle)

            _series.value =
                seriesRepository.getAllSeriesOnce()
        }
    }

    fun save() {

        val state = _uiState.value

        when (state) {

            is CreateProjectUiState -> {

                val type = state.projectType ?: return

                viewModelScope.launch {

                    val project =
                        Project(
                            id = state.draftProjectId,
                            seriesId = state.seriesId,
                            type = type,
                            fields = state.fields,
                            updatedAt = System.currentTimeMillis(),
                            startDate = System.currentTimeMillis(),
                            finishDate = null
                        )

                    repo.createProject(project)
                }
            }

            is EditProjectUiState -> {

                val original = loadedProject ?: return

                viewModelScope.launch {

                    repo.updateProject(
                        original.copy(
                            fields = state.fields,
                            seriesId = state.seriesId
                        )
                    )
                }
            }
        }
    }

    fun delete() {

        val project = loadedProject ?: return

        viewModelScope.launch {
            repo.deleteProject(project.id)
        }
    }

    override fun copyState(
        state: ProjectFormState,
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>
    ): ProjectFormState {

        return state.copyForm(
            fields = fields,
            seriesId = seriesId,
            seriesSharedFieldIds = seriesSharedFieldIds
        )
    }

    override fun markDirty(state: ProjectFormState): ProjectFormState = state

    override fun isSaving(state: ProjectFormState): Boolean = false
}