package com.meadow.feature.project.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ui.events.UiMessage
import com.meadow.feature.project.R
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.Series
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import com.meadow.feature.project.sync.drive.ProjectDriveBackupCoordinator
import com.meadow.feature.project.ui.state.*
import com.meadow.feature.project.ui.util.isArchived
import com.meadow.feature.project.ui.util.isCompleted
import com.meadow.feature.project.ui.util.readFirstTextByKeys
import com.meadow.feature.project.ui.util.readTitle
import com.meadow.feature.project.ui.util.readTitleOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ListViewModel @Inject constructor(
    private val projectRepo: ProjectRepositoryContract,
    private val seriesRepo: SeriesRepositoryContract,
    private val driveBackupCoordinator: ProjectDriveBackupCoordinator
) : ViewModel() {

    /* ─── UI STATE ───────────────────────────────── */

    private val _uiState = MutableStateFlow(ListUiState(isLoading = true))
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    /* ─── SNACKBAR SYSTEM ────────────────────────── */

    private val _uiMessages = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val uiMessages = _uiMessages.asSharedFlow()

    private suspend fun emit(@StringRes res: Int) {
        _uiMessages.emit(UiMessage.Snackbar(res))
    }

    /* ─── INITIAL LOAD ───────────────────────────── */

    init {
        observeData()
    }

    /* ─── OBSERVE PROJECTS + SERIES ──────────────── */

    private fun observeData() {

        viewModelScope.launch {

            combine(
                projectRepo.observeProjects(),
                seriesRepo.observeAllSeries()
            ) { projects, series ->

                Pair(projects, series)

            }.collect { (projects, series) ->

                val s = _uiState.value

                _uiState.value = s.copy(
                    isLoading = false,
                    allProjects = projects,
                    allSeries = series,
                    items = buildItems(
                        projects,
                        series,
                        s
                    )
                )
            }
        }
    }

    /* ─── SEARCH / FILTER / SORT UPDATES ─────────── */

    fun updateSearch(query: String) {
        updateState { it.copy(searchQuery = query) }
    }

    fun setSearchMode(mode: ListSearchMode) {
        updateState { it.copy(searchMode = mode) }
    }

    fun updateViewMode(mode: ListViewMode) {
        updateState { it.copy(viewMode = mode) }
    }

    fun updateProjectSort(sort: ProjectListSort) {
        updateState { it.copy(projectSort = sort) }
    }

    fun updateSeriesSort(sort: SeriesListSort) {
        updateState { it.copy(seriesSort = sort) }
    }

    fun updateVisibility(v: ProjectListVisibility) {
        updateState { it.copy(visibility = v) }
    }

    fun updateFilters(filters: ProjectListFilters) {
        updateState { it.copy(projectFilters = filters) }
    }

    fun clearFilters() {
        updateState { it.copy(projectFilters = ProjectListFilters()) }
    }

    /* ─── GENERIC STATE UPDATE ───────────────────── */

    private fun updateState(update: (ListUiState) -> ListUiState) {

        val newState = update(_uiState.value)

        _uiState.value = newState.copy(
            items = buildItems(
                newState.allProjects,
                newState.allSeries,
                newState
            )
        )
    }

    /* ─── BUILD MERGED LIST ─────────────────────── */

    private fun buildItems(
        projects: List<Project>,
        series: List<Series>,
        state: ListUiState
    ): List<ListItem> {

        val projectItems =
            if (state.searchMode != ListSearchMode.SERIES)
                applyProjectSearchFilterSort(projects, state)
                    .map { ListItem.ProjectItem(it) }
            else emptyList()

        val seriesItems =
            if (state.searchMode != ListSearchMode.PROJECTS)
                applySeriesSearchSort(series, state)
                    .map { ListItem.SeriesItem(it) }
            else emptyList()

        return projectItems + seriesItems
    }

    /* ─── PROJECT FILTERING ─────────────────────── */

    private fun applyProjectSearchFilterSort(
        projects: List<Project>,
        s: ListUiState
    ): List<Project> {

        val filtered =
            projects
                .filter { matchesProjectSearch(it, s.searchQuery) }
                .filter { matchesVisibility(it, s.visibility) }

        return when (s.projectSort) {

            ProjectListSort.TITLE_ASC ->
                filtered.sortedBy { it.readTitleOrNull() ?: "" }

            ProjectListSort.TITLE_DESC ->
                filtered.sortedByDescending { it.readTitleOrNull() ?: "" }

            ProjectListSort.CREATED_ASC ->
                filtered.sortedBy { it.startDate ?: Long.MAX_VALUE }

            ProjectListSort.CREATED_DESC ->
                filtered.sortedByDescending { it.startDate ?: 0 }

            ProjectListSort.UPDATED_ASC ->
                filtered.sortedBy { it.updatedAt }

            ProjectListSort.UPDATED_DESC ->
                filtered.sortedByDescending { it.updatedAt }
        }
    }

    private fun matchesProjectSearch(project: Project, query: String): Boolean {
        if (query.isBlank()) return true
        val blob = buildString {
            append(project.readTitleOrNull() ?: "")
            append("\n")

            listOf(
                project.fields.readFirstTextByKeys("description"),
                project.fields.readFirstTextByKeys("pitch"),
                project.fields.readFirstTextByKeys("premise"),
                project.fields.readFirstTextByKeys("plot")
            ).forEach { append(it ?: "") }
        }
        return blob.contains(query, ignoreCase = true)
    }

    private fun matchesVisibility(
        project: Project,
        v: ProjectListVisibility
    ): Boolean {

        if (!v.showArchived && project.isArchived()) return false
        if (!v.showCompleted && project.isCompleted()) return false

        return true
    }

    /* ─── SERIES FILTERING ─────────────────────── */

    private fun applySeriesSearchSort(
        series: List<Series>,
        s: ListUiState
    ): List<Series> {

        val filtered =
            series.filter {
                s.searchQuery.isBlank()
                        || it.title.contains(s.searchQuery, true)
            }

        return when (s.seriesSort) {

            SeriesListSort.TITLE_ASC ->
                filtered.sortedBy { it.title }

            SeriesListSort.TITLE_DESC ->
                filtered.sortedByDescending { it.title }

            SeriesListSort.PROJECT_COUNT_ASC ->
                filtered.sortedBy { it.projectIds.size }

            SeriesListSort.PROJECT_COUNT_DESC ->
                filtered.sortedByDescending { it.projectIds.size }
        }
    }

    /* ─── PROJECT ACTIONS ─────────────────────── */

    fun requestDeleteProject(project: Project) {
        _uiState.update { it.copy(pendingDeleteProject = project) }
    }

    fun confirmDeleteProject() {

        val project = _uiState.value.pendingDeleteProject ?: return

        viewModelScope.launch {

            projectRepo.deleteProject(project.id)

            _uiState.update { it.copy(pendingDeleteProject = null) }

            emit(R.string.project_deleted_success)
        }
    }

    fun syncProject(project: Project) {
        viewModelScope.launch { projectRepo.syncProject(project.id) }
    }

    fun backupProject(project: Project) {
        viewModelScope.launch {
            driveBackupCoordinator.backupProject(project.id)
        }
    }

    fun archiveProject(project: Project) {
        viewModelScope.launch {
            projectRepo.updateProjectField(
                project.id,
                "field_project_archived",
                true
            )
        }
    }

    fun completeProject(project: Project) {
        viewModelScope.launch {
            projectRepo.updateProjectField(
                project.id,
                "field_project_completed",
                true
            )
        }
    }

    /* ─── SERIES ACTIONS ─────────────────────── */

    fun requestDeleteSeries(series: Series) {
        _uiState.update { it.copy(pendingDeleteSeries = series) }
    }

    fun confirmDeleteSeries() {

        val series = _uiState.value.pendingDeleteSeries ?: return

        viewModelScope.launch {

            seriesRepo.deleteSeries(series.id)

            _uiState.update { it.copy(pendingDeleteSeries = null) }

            emit(R.string.series_deleted_success)
        }
    }

    fun requestRenameSeries(series: Series) {
        _uiState.update { it.copy(pendingRenameSeries = series) }
    }

    fun renameSeries(newTitle: String) {

        val series = _uiState.value.pendingRenameSeries ?: return

        viewModelScope.launch {

            seriesRepo.renameSeries(series.id, newTitle.trim())

            _uiState.update { it.copy(pendingRenameSeries = null) }

            emit(R.string.series_renamed_success)
        }
    }

    fun openSeriesAiContext(series: Series) {
        _uiState.update { it.copy(editingSeriesAiContext = series) }
    }

    fun closeSeriesAiContext() {
        _uiState.update { it.copy(editingSeriesAiContext = null) }
    }
    fun isDuplicateSeriesName(name: String): Boolean {
        return _uiState.value.allSeries.any {
            it.title.equals(name.trim(), ignoreCase = true)
        }
    }
    fun createSeriesInline(title: String) {
        viewModelScope.launch {
            seriesRepo.createSeries(title.trim())
        }
    }
    fun cancelDeleteProject() {
        _uiState.update { it.copy(pendingDeleteProject = null) }
    }
    fun cancelDeleteSeries() {
        _uiState.update { it.copy(pendingDeleteSeries = null) }
    }
    fun cancelRenameSeries() {
        _uiState.update { it.copy(pendingRenameSeries = null) }
    }
    fun startEditSeries(series: Series) {

        val projects = _uiState.value.allProjects

        val inSeries = projects.filter { it.id in series.projectIds }

        val available = projects.filter { it.id !in series.projectIds }

        _uiState.update {
            it.copy(
                editingSeries = series,
                projectsInSeries = inSeries,
                availableProjects = available
            )
        }
    }
    fun closeEditSeries() {
        _uiState.update {
            it.copy(
                editingSeries = null,
                projectsInSeries = emptyList(),
                availableProjects = emptyList(),
                projectSearchQuery = ""
            )
        }
    }
    fun updateProjectSearch(query: String) {
        _uiState.update { it.copy(projectSearchQuery = query) }
    }
    fun addProjectToSeries(projectId: String) {

        val series = _uiState.value.editingSeries ?: return

        viewModelScope.launch {
            seriesRepo.addProjectToSeries(series.id, projectId)
        }
    }
    fun removeProjectFromSeries(projectId: String) {

        val series = _uiState.value.editingSeries ?: return

        viewModelScope.launch {
            seriesRepo.removeProjectFromSeries(series.id, projectId)
        }
    }
}