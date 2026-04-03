@file:OptIn(ExperimentalLayoutApi::class)

package com.meadow.feature.project.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.meadow.core.ai.ui.components.AiContextEditor
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.events.CollectUiMessages
import com.meadow.core.ui.layout.BoxCenter
import com.meadow.core.ui.layout.MeadowDashboard
import com.meadow.core.ui.layout.MeadowDashboardSlider
import com.meadow.core.ui.layout.MeadowWidthClass
import com.meadow.core.ui.layout.rememberMeadowWindowInfo
import com.meadow.core.ui.locale.LocalizedContext
import com.meadow.core.utils.datetime.DateUtils
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.KebabAction
import com.meadow.feature.common.ui.screens.FeatureDisabledScreen
import com.meadow.feature.project.R
import com.meadow.feature.project.aicontext.domain.AiScopeKeys
import com.meadow.feature.project.data.preferences.ProjectFeaturePreferences
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import com.meadow.feature.project.domain.model.Series
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import com.meadow.feature.project.ui.components.CreateOrSelectSeriesDialog
import com.meadow.feature.project.ui.components.CreateSeriesDialog
import com.meadow.feature.project.ui.components.ProjectDeleteConfirmDialog
import com.meadow.feature.project.ui.navigation.ProjectRoutes
import com.meadow.feature.project.ui.state.ProjectDashboardUiState
import com.meadow.feature.project.ui.state.ProjectFeatureUi
import com.meadow.feature.project.ui.util.resolveCoverImageOrNull
import com.meadow.feature.project.ui.viewmodel.DashboardViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProjectRepoEntryPoint {
    fun projectRepository(): ProjectRepositoryContract
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SeriesRepoEntryPoint {
    fun seriesRepository(): SeriesRepositoryContract
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProjectFeatureSpecEntryPoint {
    fun projectFeatureSpecs(): Set<ProjectFeatureSpec>
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    featureContextState: FeatureContextState,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val project = state.project
    val context = LocalContext.current
    val localized = LocalizedContext.current
    val windowInfo = rememberMeadowWindowInfo()
    val scope = rememberCoroutineScope()
    val aiContext = viewModel.aiContext
    val showAiEditor = viewModel.isAiEditorOpen
    val featureSpecs = remember {
        EntryPointAccessors.fromApplication(
            context,
            ProjectFeatureSpecEntryPoint::class.java
        ).projectFeatureSpecs()
    }
    val seriesRepo = remember {
        EntryPointAccessors.fromApplication(
            context,
            SeriesRepoEntryPoint::class.java
        ).seriesRepository()
    }

    var disabledFeatureName by remember { mutableStateOf<String?>(null) }
    var showCreateOrSelectSeriesDialog by remember { mutableStateOf(false) }
    var showCreateSeriesDialog by remember { mutableStateOf(false) }
    var availableSeries by remember { mutableStateOf<List<Series>>(emptyList()) }
    var optimisticSeriesId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(project?.seriesId, project?.id) { optimisticSeriesId = null }
    val effectiveSeriesId: String? = optimisticSeriesId ?: project?.seriesId
    val projectFeaturePrefs =
        project?.let {
            remember(it.id) {
                ProjectFeaturePreferences(
                    context = context,
                    projectId = it.id,
                    projectType = it.type
                )
            }
        }
    val featureEnabledStates =
        projectFeaturePrefs?.features
            ?.collectAsStateWithLifecycle(initialValue = emptyMap())
            ?.value ?: emptyMap()

    LaunchedEffect(project?.id) { projectFeaturePrefs?.ensureDefaults() }
    val projectFeatures =
        remember(project?.id, featureEnabledStates, featureSpecs) {
            if (project == null) emptyList() else {
                featureEnabledStates.mapNotNull { (key, enabled) ->
                    val featureKey = key.name.removePrefix("project_${project.id}_")
                    val spec = featureSpecs.firstOrNull { it.key == featureKey }
                        ?: return@mapNotNull null
                    ProjectFeatureUi(
                        key = key,
                        enabled = enabled,
                        titleRes = spec.titleRes,
                        icon = spec.icon,
                        route = spec.routeForProject(project.id),
                        count = 0
                    )
                }
            }
        }

    if (disabledFeatureName != null) {
        FeatureDisabledScreen(
            featureName = disabledFeatureName!!,
            onBack = { disabledFeatureName = null }
        )
        BackHandler { disabledFeatureName = null }
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }
    CollectUiMessages(viewModel.uiMessages, snackbarHostState)

    if (showCreateSeriesDialog) {
        CreateSeriesDialog(
            isDuplicate = { name -> availableSeries.any { it.title.equals(name, true) } },
            onConfirm = { title ->
                viewModel.createSeriesFromProject(title)
                showCreateSeriesDialog = false
            },
            onDismiss = { showCreateSeriesDialog = false }
        )
    }

    if (showCreateOrSelectSeriesDialog) {
        CreateOrSelectSeriesDialog(
            series = availableSeries,
            selectedId = effectiveSeriesId,
            isDuplicate = { name -> availableSeries.any { it.title.equals(name, true) } },
            onSelect = { seriesId ->
                if (seriesId != null) {
                    optimisticSeriesId = seriesId
                    viewModel.addProjectToSeries(seriesId)
                }
            },
            onCreate = { title -> viewModel.createSeriesFromProject(title) },
            onDismiss = { showCreateOrSelectSeriesDialog = false },
            onConfirm = { showCreateOrSelectSeriesDialog = false }
        )
    }

    LaunchedEffect(project?.id, effectiveSeriesId) {
        if (project == null) return@LaunchedEffect
        val kebab = mutableListOf<KebabAction>()

        kebab += KebabAction(localized.getString(CoreUiR.string.action_edit)) {
            navController.navigate(ProjectRoutes.projectEdit(project.id))
        }
        kebab += KebabAction(localized.getString(CoreUiR.string.action_history)) {
            navController.navigate(ProjectRoutes.projectHistory(project.id))
        }
        kebab += KebabAction(localized.getString(CoreUiR.string.action_sync)) {
            viewModel.syncProjectNow()
        }
        kebab += KebabAction(localized.getString(CoreUiR.string.action_backup)) {
            viewModel.backupProjectToDrive()
        }

        if (effectiveSeriesId == null) {
            kebab += KebabAction(localized.getString(R.string.convert_to_series)) {
                optimisticSeriesId = project.id
                viewModel.convertProjectToSeries()
            }
            kebab += KebabAction(localized.getString(R.string.add_to_series)) {
                scope.launch {
                    val list =
                        runCatching { seriesRepo.getAllSeriesOnce() }.getOrDefault(emptyList())
                    if (list.isEmpty()) showCreateSeriesDialog = true
                    else {
                        availableSeries = list
                        showCreateOrSelectSeriesDialog = true
                    }
                }
            }
        } else {
            kebab += KebabAction(localized.getString(R.string.revert_to_project)) {
                optimisticSeriesId = null
                viewModel.revertSeriesToProject()
            }
            kebab += KebabAction(localized.getString(R.string.remove_from_series)) {
                optimisticSeriesId = null
                viewModel.removeProjectFromSeries()
            }
        }

        kebab += KebabAction(localized.getString(CoreUiR.string.action_delete)) {
            viewModel.requestDelete()
        }

        featureContextState.setKebabActions(kebab)
    }

    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            SnackbarHost(hostState = snackbarHostState)
            when {
                state.isLoading -> BoxCenter { CircularProgressIndicator() }
                project == null -> BoxCenter {
                    Text(stringResource(R.string.error_project_not_found))
                }
                else -> project?.let { projectItem ->
                    val isSeriesDashboard = projectItem.seriesId == projectItem.id
                    MeadowDashboard(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                horizontal =
                                    if (windowInfo.widthClass == MeadowWidthClass.Phone) 16.dp else 24.dp
                            )
                            .padding(
                                top = WindowInsets.statusBars
                                    .asPaddingValues()
                                    .calculateTopPadding()
                            ),
                        tabs = listOf(
                            stringResource(R.string.dashboard_overview),
                            stringResource(R.string.dashboard_in_this_series)
                        ),
                        header = {
                            HeaderBlock(
                                cover = state.coverImagePath,
                                title = state.title,
                                projectType =
                                    if (isSeriesDashboard)
                                        stringResource(R.string.series)
                                    else
                                        state.projectTypeLine,
                                smallLine = state.smallLine,
                                lastUpdated = state.updatedAt,
                                lastSynced = state.project?.syncMeta?.lastFirestoreSyncAt,
                                lastBackup = state.project?.syncMeta?.lastDriveBackupAt,
                                onClick = {}
                            )
                        }
                    ) { selectedTab ->
                        when (selectedTab) {
                            /* ─── OVERVIEW TAB ───────────────────────── */
                            0 -> {
                                MeadowDashboardSlider(
                                    pageCount = if (state.description.isNotBlank()) 3 else 2,
                                    icon = CoreUiR.drawable.ic_star
                                ) { page ->

                                    when (page) {

                                        0 -> Text(state.description)

                                        1 -> OverviewContent(state)

                                        2 -> DetailsContent(state)
                                    }
                                }
                            }
                            /* ─── SERIES TAB ───────────────────────── */
                            1 -> {
                                val seriesId = projectItem.seriesId

                                if (isSeriesDashboard) {
                                    /* SERIES DASHBOARD → show grid */
                                    SeriesGrid(
                                        seriesId = projectItem.id,
                                        onOpenProject = navController::navigate
                                    )
                                }  else if (seriesId != null) {
                                    /* PROJECT THAT BELONGS TO A SERIES */
                                    SeriesGrid(
                                        seriesId = seriesId,
                                        onOpenProject = navController::navigate
                                    )
                                } else {
                                    /* PROJECT NOT IN SERIES */
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.series_no_projects),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    val list =
                                                        runCatching { seriesRepo.getAllSeriesOnce() }
                                                            .getOrDefault(emptyList())
                                                    if (list.isEmpty()) showCreateSeriesDialog = true
                                                    else {
                                                        availableSeries = list
                                                        showCreateOrSelectSeriesDialog = true
                                                    }

                                                }
                                            }
                                        ) {
                                            Text(stringResource(R.string.add_to_series))
                                        }
                                    }

                                }
                            }

                        }

                    }

                }

            }

        }

    }

    if (state.showDeleteConfirm) {
        ProjectDeleteConfirmDialog(
            projectTitle = state.title,
            onConfirmDelete = {
                viewModel.cancelDelete()
                viewModel.deleteProject {
                    featureContextState.clearProject()
                    featureContextState.clearKebabActions()
                    navController.navigate(ProjectRoutes.projectList()) {
                        popUpTo(ProjectRoutes.PROJECT_LIST) { inclusive = true }
                    }
                }
            },
            onDismiss = { viewModel.cancelDelete() }
        )
    }

    BackHandler(enabled = state.showDeleteConfirm) { viewModel.cancelDelete() }

    if (showAiEditor && project != null) {
        val scopeKey = AiScopeKeys.project(project.id)
        AiContextEditor(
            scopeKey = scopeKey,
            onDismiss = viewModel::closeAiEditor
        )
    }

}

@Composable
private fun HeaderBlock(
    cover: Any?,
    title: String,
    projectType: String,
    smallLine: String,
    lastUpdated: Long?,
    lastSynced: Long?,
    lastBackup: Long?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.Top
    ) {

        /* ─── COVER IMAGE ───────────────────────── */

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(20.dp)
                )
                .clickable { onClick() }
        ) {
            if (cover != null) {
                AsyncImage(
                    model = cover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        /* ─── PROJECT META ───────────────────────── */

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.clickable { onClick() }
            )

            if (projectType.isNotBlank()) {
                Text(
                    text = projectType,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (smallLine.isNotBlank()) {
                Text(
                    text = smallLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val updatedLabel = lastUpdated?.let {
                stringResource(
                    CoreUiR.string.sort_updated,
                    DateUtils.millisToDisplay(it)
                )
            }

            val syncedLabel = lastSynced?.let {
                stringResource(
                    CoreUiR.string.action_sync,
                    DateUtils.millisToDisplay(it)
                )
            }

            val backupLabel = lastBackup?.let {
                stringResource(
                    CoreUiR.string.action_backup,
                    DateUtils.millisToDisplay(it)
                )
            }

            val metaLine = listOfNotNull(
                updatedLabel,
                syncedLabel,
                backupLabel
            ).joinToString(" • ")

            if (metaLine.isNotBlank()) {
                Text(
                    text = metaLine,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
private fun OverviewContent(state: ProjectDashboardUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val items = listOf(
            stringResource(R.string.field_project_pitch) to state.overview.pitch,
            stringResource(R.string.field_project_premise) to state.overview.premise,
            stringResource(R.string.field_project_promise) to state.overview.promise,
            stringResource(R.string.field_project_plot) to state.overview.plot
        )
        items.forEach { (label, value) ->
            if (!value.isNullOrBlank()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
@Composable
private fun DetailsContent(state: ProjectDashboardUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val groups = listOf(
            stringResource(R.string.field_project_audience) to state.chips.audience,
            stringResource(R.string.field_project_genre) to state.chips.genre,
            stringResource(R.string.field_project_elements) to state.chips.elements,
            stringResource(R.string.field_project_rating) to state.chips.rating,
            stringResource(R.string.field_project_warnings) to state.chips.warnings
        )
        groups.forEach { (label, values) ->
            if (values.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        values.forEach { value ->
                            AssistChip(
                                onClick = {},
                                label = { Text(value) }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun SeriesGrid(
    seriesId: String,
    onOpenProject: (String) -> Unit
) {
    val context = LocalContext.current

    val repo = remember {
        EntryPointAccessors.fromApplication(
            context,
            ProjectRepoEntryPoint::class.java
        ).projectRepository()
    }

    val projects by repo.observeProjects()
        .map { list -> list.filter { it.seriesId == seriesId } }
        .collectAsState(initial = emptyList())

    if (projects.isEmpty()) {
        Text(
            text = stringResource(R.string.series_no_projects),
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        projects.forEach { project ->

            Surface(
                onClick = {
                    onOpenProject(ProjectRoutes.projectDashboard(project.id))
                },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.size(110.dp)
            ) {
                AsyncImage(
                    model = project.resolveCoverImageOrNull(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
