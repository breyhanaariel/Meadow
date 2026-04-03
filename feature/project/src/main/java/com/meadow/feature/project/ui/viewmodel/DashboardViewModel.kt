package com.meadow.feature.project.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.events.UiMessage
import com.meadow.feature.project.R
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectFeatureCountProvider
import com.meadow.feature.project.domain.model.ProjectType
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.usecase.AddProjectToSeriesUseCase
import com.meadow.feature.project.domain.usecase.ConvertToSeriesUseCase
import com.meadow.feature.project.domain.usecase.CreateSeriesAndAttachUseCase
import com.meadow.feature.project.domain.usecase.RemoveProjectFromSeriesUseCase
import com.meadow.feature.project.domain.usecase.RevertSeriesToProjectUseCase
import com.meadow.feature.project.sync.drive.ProjectDriveBackupCoordinator
import com.meadow.feature.project.ui.state.*
import com.meadow.feature.project.ui.util.readFirstTextByKeys
import com.meadow.feature.project.ui.util.readTitleOrNull
import com.meadow.feature.project.ui.util.resolveCoverImageOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repo: ProjectRepositoryContract,
    private val addToSeries: AddProjectToSeriesUseCase,
    private val convertToSeries: ConvertToSeriesUseCase,
    private val createSeriesAndAttach: CreateSeriesAndAttachUseCase,
    private val removeFromSeries: RemoveProjectFromSeriesUseCase,
    private val revertSeriesToProject: RevertSeriesToProjectUseCase,
    private val driveBackup: ProjectDriveBackupCoordinator,
    private val countProviders: Set<@JvmSuppressWildcards ProjectFeatureCountProvider>,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /* ─── CORE UI STATE ─────────────────────────── */

    private val _uiState = MutableStateFlow(ProjectDashboardUiState())
    val uiState: StateFlow<ProjectDashboardUiState> = _uiState.asStateFlow()

    /* ─── AI EDITOR VISIBILITY (ONLY THIS IS NEEDED) ───────── */

    var aiContext by mutableStateOf<Any?>(null) // not used, but kept for screen compatibility
        private set

    var isAiEditorOpen by mutableStateOf(false)
        private set

    /* ─── UI MESSAGE PIPELINE ──────────────────── */

    private val _uiMessages = MutableSharedFlow<UiMessage>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiMessages = _uiMessages.asSharedFlow()

    /* ─── NAV ARGUMENTS ────────────────────────── */

    private val projectId: String =
        requireNotNull(savedStateHandle["projectId"]) {
            "projectId is required"
        }

    init {
        observeProject()
    }

    /* ─── AI EDITOR CONTROL ───────────────────── */

    fun openAiEditor() {
        isAiEditorOpen = true
    }

    fun closeAiEditor() {
        isAiEditorOpen = false
    }

    /* ─── PROJECT OBSERVATION ─────────────────── */

    private fun observeProject() {
        repo.observeProject(projectId)
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { project ->
                _uiState.update { current ->
                    if (project == null) {
                        current.copy(isLoading = false)
                    } else {
                        project.toDashboardUi().copy(sync = current.sync)
                    }
                }
                project?.let { observeFeatureCounts(it.id) }

            }
            .catch { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
            .launchIn(viewModelScope)
    }

    /* ─── SYNC + BACKUP ───────────────────────── */

    fun syncProjectNow() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(sync = ProjectSyncUiState(status = ProjectSyncStatus.SYNCING))
            }
            runCatching { repo.syncProject(projectId) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            sync = ProjectSyncUiState(
                                status = ProjectSyncStatus.IDLE,
                                lastSyncedAt = System.currentTimeMillis()
                            )
                        )
                    }
                    _uiMessages.tryEmit(UiMessage.Snackbar(CoreUiR.string.sync_success))
                }
        }
    }

    fun backupProjectToDrive() {
        viewModelScope.launch {
            runCatching { driveBackup.backupProject(projectId) }
                .onSuccess {
                    _uiMessages.tryEmit(UiMessage.Snackbar(CoreUiR.string.backup_success))
                }
        }
    }

    /* ─── UI STATE MAPPING ────────────────────── */

    private fun Project.toDashboardUi(): ProjectDashboardUiState {
        val audienceRaw = fields.readFirstTextByKeys("audience", "project.audience")
        val ratingRaw = fields.readFirstTextByKeys("rating", "project.rating")
        val formatRaw = fields.readFirstTextByKeys("format", "project.format")
        val statusRaw = fields.readFirstTextByKeys("status", "project.status")

        fun localizeKey(key: String?): String? {
            if (key.isNullOrBlank()) return null
            val resId = appContext.resources.getIdentifier(
                key,
                "string",
                appContext.packageName
            )
            return if (resId != 0) appContext.getString(resId) else key
        }

        val audience = localizeKey(audienceRaw)
        val rating = localizeKey(ratingRaw)
        val format = localizeKey(formatRaw)
        val status = localizeKey(statusRaw)

        val projectTypeLabel =
            type.takeIf { it != ProjectType.UNKNOWN }
                ?.name
                ?.replace("_", " ")
                ?.lowercase()
                ?.replaceFirstChar { it.uppercase() }

        return ProjectDashboardUiState(
            isLoading = false,
            project = this,
            title = readTitleOrNull() ?: "",
            projectTypeLine = projectTypeLabel ?: "",
            description = fields.readFirstTextByKeys("description") ?: "",
            coverImagePath = resolveCoverImageOrNull(),
            smallLine = listOfNotNull(format, status).joinToString(" • "),
            updatedAt = this.updatedAt ?: this.syncMeta.localUpdatedAt,
            overview = ProjectOverviewUi(
                pitch = fields.readFirstTextByKeys("pitch"),
                premise = fields.readFirstTextByKeys("premise"),
                promise = fields.readFirstTextByKeys("promise"),
                plot = fields.readFirstTextByKeys("plot")
            ),
            chips = ProjectChipsUi(
                audience = fields.readChipValuesByKeys("audience").mapNotNull { localizeKey(it) },
                genre = fields.readChipValuesByKeys("genre").mapNotNull { localizeKey(it) },
                elements = fields.readChipValuesByKeys("elements").mapNotNull { localizeKey(it) },
                rating = fields.readChipValuesByKeys("rating").mapNotNull { localizeKey(it) },
                warnings = fields.readChipValuesByKeys("warnings").mapNotNull { localizeKey(it) }
            )
        )
    }

    /* ─── SERIES / DELETE / ORDER ─────────────── */

    fun requestDelete() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    fun deleteProject(onDeleted: () -> Unit) {
        viewModelScope.launch {
            runCatching { repo.deleteProject(projectId) }
                .onSuccess {
                    _uiMessages.tryEmit(UiMessage.Snackbar(R.string.project_deleted_success))
                    onDeleted()
                }
        }
    }

    fun createSeriesFromProject(title: String) =
        viewModelScope.launch { createSeriesAndAttach(projectId, title) }

    fun convertProjectToSeries() =
        viewModelScope.launch { convertToSeries(projectId) }

    fun revertSeriesToProject() =
        viewModelScope.launch { revertSeriesToProject(projectId) }

    fun addProjectToSeries(seriesId: String) =
        viewModelScope.launch { addToSeries(projectId, seriesId) }

    fun removeProjectFromSeries() =
        viewModelScope.launch { removeFromSeries(projectId) }

    fun saveFeatureOrder(orderedFeatureIds: List<String>) {
        val project = _uiState.value.project ?: return
        viewModelScope.launch {
            repo.updateProjectField(
                project.id,
                "project.feature_order",
                orderedFeatureIds.joinToString(",")
            )
        }
    }
    /* ─── FEATURE COUNTS ─────────────── */

    private fun observeFeatureCounts(projectId: String) {

        if (countProviders.isEmpty()) return

        combine(
            countProviders.map { provider ->
                provider.observeCount(projectId)
                    .map { count -> provider.key to count }
            }
        ) { pairs ->
            pairs.toMap()
        }
            .onEach { counts ->
                _uiState.update { it.copy(featureCounts = counts) }
            }
            .launchIn(viewModelScope)
    }
}

/* ─── CHIP PARSER ─── */

private fun List<FieldWithValue>.readChipValuesByKeys(vararg keys: String): List<String> =
    readFirstTextByKeys(*keys)
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        ?: emptyList()

// private fun List<FieldWithValue>.readChipValuesByKeys(vararg keys: String): List<String> =
//    filter { it.definition.id in keys }
//        .mapNotNull { it.value.text }
