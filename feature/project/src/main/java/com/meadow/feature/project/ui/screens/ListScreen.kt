package com.meadow.feature.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meadow.core.ai.ui.components.AiContextEditor
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.*
import com.meadow.core.ui.events.CollectUiMessages
import com.meadow.core.ui.theme.LocalMeadowThemeVariant
import com.meadow.core.ui.theme.ThemeIconResolver
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.KebabAction
import com.meadow.feature.project.R
import com.meadow.feature.project.aicontext.domain.AiScopeKeys
import com.meadow.feature.project.ui.components.*
import com.meadow.feature.project.ui.navigation.ProjectRoutes
import com.meadow.feature.project.ui.state.*
import com.meadow.feature.project.ui.util.readTitle
import com.meadow.feature.project.ui.viewmodel.ListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    featureContextState: FeatureContextState,
    viewModel: ListViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showVisibilitySheet by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showCreateSeriesDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    CollectUiMessages(
        messages = viewModel.uiMessages,
        snackbarHostState = snackbarHostState
    )

    LaunchedEffect(Unit) {
        featureContextState.setKebabActions(
            listOf(
                KebabAction(context.getString(R.string.action_create_project)) {
                    navController.navigate(ProjectRoutes.projectCreate())
                },
                KebabAction(context.getString(R.string.action_create_series)) {
                    showCreateSeriesDialog = true
                }
            )
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },

        floatingActionButton = {

            val themeVariant = LocalMeadowThemeVariant.current

            Column(
                horizontalAlignment = Alignment.End
            ) {

                if (showFabMenu) {

                    MeadowButton(
                        text = stringResource(R.string.action_create_series),
                        onClick = {
                            showFabMenu = false
                            showCreateSeriesDialog = true
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    MeadowButton(
                        text = stringResource(R.string.action_create_project),
                        onClick = {
                            showFabMenu = false
                            navController.navigate(ProjectRoutes.projectCreate())
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                }

                MeadowFab(
                    style = MeadowFabStyle.ImageOnly,
                    painterResId = ThemeIconResolver.fabmenu(themeVariant),
                    onClick = { showFabMenu = !showFabMenu }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Spacer(Modifier.height(8.dp))

            ListSearchToolbar(
                query = state.searchQuery,
                onQueryChange = viewModel::updateSearch,
                mode = state.searchMode,
                viewMode = state.viewMode,
                onViewModeChange = viewModel::updateViewMode,
                onSortClick = { showSortSheet = true },
                onVisibilityClick = { showVisibilitySheet = true },
                onFilterClick = { showFilterSheet = true }
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilterChip(
                    selected = state.searchMode != ListSearchMode.SERIES,
                    onClick = { viewModel.setSearchMode(ListSearchMode.PROJECTS) },
                    label = { Text(stringResource(R.string.projects_title)) }
                )

                FilterChip(
                    selected = state.searchMode != ListSearchMode.PROJECTS,
                    onClick = { viewModel.setSearchMode(ListSearchMode.SERIES) },
                    label = { Text(stringResource(R.string.series)) }
                )

                FilterChip(
                    selected = state.searchMode == ListSearchMode.BOTH,
                    onClick = { viewModel.setSearchMode(ListSearchMode.BOTH) },
                    label = { Text(stringResource(R.string.series_and_projects)) }
                )
            }

            Spacer(Modifier.height(8.dp))

            when {

                state.isLoading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.items.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(CoreUiR.string.no_results),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 8.dp
                        )
                    ) {

                        items(
                            items = state.items,
                            key = {
                                when (it) {
                                    is ListItem.ProjectItem -> it.project.id
                                    is ListItem.SeriesItem -> it.series.id
                                }
                            }
                        ) { item ->

                            when (item) {

                                is ListItem.ProjectItem -> {

                                    if (state.viewMode == ListViewMode.GRID) {

                                        ProjectCardGrid(
                                            project = item.project,
                                            firestoreSyncUi =
                                                state.syncByProjectId[item.project.id]
                                                    ?: state.globalSync,
                                            onOpen = {
                                                navController.navigate(
                                                    ProjectRoutes.projectDashboard(item.project.id)
                                                )
                                            },
                                            onEdit = {
                                                navController.navigate(
                                                    ProjectRoutes.projectEdit(item.project.id)
                                                )
                                            },
                                            onDelete = {
                                                viewModel.requestDeleteProject(item.project)
                                            }
                                        )

                                    } else {

                                        ProjectCard(
                                            project = item.project,
                                            firestoreSyncUi =
                                                state.syncByProjectId[item.project.id]
                                                    ?: state.globalSync,
                                            onOpen = {
                                                navController.navigate(
                                                    ProjectRoutes.projectDashboard(item.project.id)
                                                )
                                            },
                                            onEdit = {
                                                navController.navigate(
                                                    ProjectRoutes.projectEdit(item.project.id)
                                                )
                                            },
                                            onFirestoreSync = {
                                                viewModel.syncProject(item.project)
                                            },
                                            onDriveBackup = {
                                                viewModel.backupProject(item.project)
                                            },
                                            onArchive = {
                                                viewModel.archiveProject(item.project)
                                            },
                                            onComplete = {
                                                viewModel.completeProject(item.project)
                                            },
                                            onDelete = {
                                                viewModel.requestDeleteProject(item.project)
                                            }
                                        )
                                    }
                                }

                                is ListItem.SeriesItem -> {

                                    if (state.viewMode == ListViewMode.GRID) {

                                        SeriesCardGrid(
                                            series = item.series,
                                            onOpen = {
                                                viewModel.startEditSeries(item.series)
                                            },
                                            onRename = {
                                                viewModel.requestRenameSeries(item.series)
                                            },
                                            onEditAiContext = {
                                                viewModel.openSeriesAiContext(item.series)
                                            },
                                            onDelete = {
                                                viewModel.requestDeleteSeries(item.series)
                                            }
                                        )

                                    } else {

                                        SeriesCard(
                                            series = item.series,
                                            onOpen = {
                                                viewModel.startEditSeries(item.series)
                                            },
                                            onRename = {
                                                viewModel.requestRenameSeries(item.series)
                                            },
                                            onEditAiContext = {
                                                viewModel.openSeriesAiContext(item.series)
                                            },
                                            onDelete = {
                                                viewModel.requestDeleteSeries(item.series)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showSortSheet) {

            MeadowBottomSheet(
                title = stringResource(CoreUiR.string.action_sort),
                onDismiss = { showSortSheet = false }
            ) {

                if (state.searchMode == ListSearchMode.SERIES) {

                    SeriesSortContent(
                        currentSort = state.seriesSort,
                        onSortChange = {
                            viewModel.updateSeriesSort(it)
                            showSortSheet = false
                        }
                    )

                } else {

                    ProjectSortContent(
                        currentSort = state.projectSort,
                        onSortChange = {
                            viewModel.updateProjectSort(it)
                            showSortSheet = false
                        }
                    )
                }
            }
        }

        if (showVisibilitySheet) {

            MeadowBottomSheet(
                title = stringResource(CoreUiR.string.action_visibility),
                onDismiss = { showVisibilitySheet = false }
            ) {

                ProjectVisibilityContent(
                    state = state.visibility,
                    onChange = viewModel::updateVisibility,
                    onClear = {
                        viewModel.updateVisibility(ProjectListVisibility())
                        showVisibilitySheet = false
                    }
                )
            }
        }

        if (showFilterSheet) {

            MeadowBottomSheet(
                title = stringResource(CoreUiR.string.action_filter),
                onDismiss = { showFilterSheet = false }
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {

                    ProjectFilterContent(
                        state = state.projectFilters,
                        options = state.filterOptions,
                        onChange = viewModel::updateFilters,
                        onClear = {
                            viewModel.clearFilters()
                            showFilterSheet = false
                        }
                    )
                }
            }
        }
        if (showCreateSeriesDialog) {
            CreateSeriesDialog(
                isDuplicate = viewModel::isDuplicateSeriesName,
                onConfirm = {
                    viewModel.createSeriesInline(it)
                    showCreateSeriesDialog = false
                },
                onDismiss = { showCreateSeriesDialog = false }
            )
        }

        state.pendingDeleteProject?.let { project ->
            ProjectDeleteConfirmDialog(
                projectTitle = project.readTitle(),
                onConfirmDelete = { viewModel.confirmDeleteProject() },
                onDismiss = { viewModel.cancelDeleteProject() }
            )
        }

        state.pendingDeleteSeries?.let { series ->
            SeriesDeleteConfirmDialog(
                seriesTitle = series.title,
                onConfirmDelete = { viewModel.confirmDeleteSeries() },
                onDismiss = { viewModel.cancelDeleteSeries() }
            )
        }

        state.pendingRenameSeries?.let { series ->

            RenameSeriesDialog(
                title = stringResource(CoreUiR.string.action_rename),
                initial = series.title,
                isDuplicate = viewModel::isDuplicateSeriesName,
                onConfirm = viewModel::renameSeries,
                onDismiss = { viewModel.cancelRenameSeries() }
            )
        }
        state.editingSeries?.let { series ->

            EditSeriesProjectsDialog(
                seriesTitle = series.title,
                projectsInSeries = state.projectsInSeries,
                availableProjects = state.availableProjects,
                projectSearchQuery = state.projectSearchQuery,
                onSearchChange = viewModel::updateProjectSearch,
                onAddProject = viewModel::addProjectToSeries,
                onRemoveProject = viewModel::removeProjectFromSeries,
                onDismiss = viewModel::closeEditSeries
            )
        }
        state.editingSeriesAiContext?.let { series ->

            AiContextEditor(
                scopeKey = AiScopeKeys.series(series.id),
                onDismiss = viewModel::closeSeriesAiContext
            )
        }
    }
}