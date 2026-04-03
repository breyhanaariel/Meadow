package com.meadow.feature.catalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowBottomSheet
import com.meadow.core.ui.components.MeadowCardGridContainer
import com.meadow.core.ui.components.MeadowFab
import com.meadow.core.ui.components.MeadowFabStyle
import com.meadow.core.ui.locale.LocalizedContext
import com.meadow.core.ui.theme.LocalMeadowThemeVariant
import com.meadow.core.ui.theme.ThemeIconResolver
import com.meadow.feature.catalog.R as R
import com.meadow.feature.catalog.ui.components.*
import com.meadow.feature.catalog.ui.navigation.CatalogRoutes
import com.meadow.feature.catalog.ui.state.*
import com.meadow.feature.catalog.ui.viewmodel.CatalogListViewModel
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.KebabAction
import com.meadow.feature.project.R as ProjectR
import com.meadow.feature.project.ui.components.ProjectSelectorDialog
import com.meadow.feature.project.ui.components.SeriesSelectorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogListScreen(
    navController: NavController,
    featureContextState: FeatureContextState,
    viewModel: CatalogListViewModel = hiltViewModel()
) {
    /* ───────────────── UI STATE ───────────────── */

    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(featureContextState.context.value.projectId) {
        val projectId = featureContextState.context.value.projectId
        if (projectId != null) {
            viewModel.setScope(CatalogListScope.Project(projectId))
        }
    }
    val context = LocalContext.current
    val localized = LocalizedContext.current
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showProjectPicker by remember { mutableStateOf(false) }
    var showSeriesPicker by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val themeVariant = LocalMeadowThemeVariant.current

    /* ───────────────── KEBAB  ───────────────── */
    LaunchedEffect(state.scope) {
        val actions = mutableListOf<KebabAction>()
        val scope = state.scope

        if (scope is CatalogListScope.Project) {
            actions += KebabAction(
                localized.getString(R.string.action_create_catalog_item)
            ) {
                navController.navigate(
                    CatalogRoutes.catalogCreateItem(scope.projectId)
                )
            }
        }

        actions += KebabAction(
            localized.getString(R.string.action_backup_catalog)
        ) {
            viewModel.backupAllVisibleToSheets()
        }

        if (scope !is CatalogListScope.Global) {
            actions += KebabAction(
                localized.getString(R.string.scope_global)
            ) {
                viewModel.setScope(CatalogListScope.Global)
            }
        }

        actions += KebabAction(
            localized.getString(ProjectR.string.choose_project)
        ) { showProjectPicker = true }

        actions += KebabAction(
            localized.getString(ProjectR.string.choose_series)
        ) { showSeriesPicker = true }

        featureContextState.setKebabActions(actions)
    }

    /* ───────────────── AUTO SCROLL ───────────────── */
    LaunchedEffect(
        state.searchQuery,
        state.sortMode,
        state.filters,
        state.scope
    ) {
        listState.scrollToItem(0)
    }

    /* ───────────────── SCREEN CONTENT ───────────────── */
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            if (state.scope is CatalogListScope.Project) {
                MeadowFab(
                    style = MeadowFabStyle.ImageOnly,
                    painterResId = ThemeIconResolver.fabmenu(themeVariant),
                    onClick = {
                        val projectId =
                            (state.scope as? CatalogListScope.Project)?.projectId
                                ?: return@MeadowFab
                        navController.navigate(
                            CatalogRoutes.catalogCreateItem(projectId)
                        )
                    }
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

            /* ───────────── SEARCH BOX ───────────── */
            CatalogSearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onQueryChange
            )

            Spacer(Modifier.height(2.dp))

            /* ───────────── TOOLBAR ───────────── */
            CatalogListToolbar(
                viewMode = state.viewMode,
                filterActive = state.filters.isActive(),
                onViewModeChange = viewModel::updateViewMode,
                onSortClick = { showSortSheet = true },
                onFilterClick = { showFilterSheet = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(2.dp))

            /* ───────────── RESULTS ───────────── */
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.allItems.isEmpty() -> {
                    EmptyNoCatalogItemsCard(
                        showCreateButton = state.scope is CatalogListScope.Project,
                        onCreate = {
                            val projectId =
                                (state.scope as? CatalogListScope.Project)?.projectId
                                    ?: return@EmptyNoCatalogItemsCard
                            navController.navigate(
                                CatalogRoutes.catalogCreateItem(projectId)
                            )
                        }
                    )
                }
                state.items.isEmpty() -> {
                    EmptyResultsCard(stringResource(R.string.catalog_no_results))
                }

                state.viewMode == CatalogListViewMode.GRID -> {
                    MeadowCardGridContainer(minCardWidth = 90.dp) {
                        gridItems(
                            state.items,
                            key = { it.id }
                        ) { item ->
                            CatalogCardGrid(
                                item = item,
                                iconResId = item.iconResId,
                                title = item.title ?: "",
                                onOpen = {
                                    navController.navigate(
                                        CatalogRoutes.catalogItem(item.id)
                                    )
                                },
                                onEdit = {
                                    navController.navigate(
                                        CatalogRoutes.catalogEditItem(item.id)
                                    )
                                },
                                onDriveBackup = {
                                    viewModel.backupCatalogItem(item.id)
                                },
                                onDelete = {
                                    viewModel.requestDelete(item)
                                }
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    ) {
                        items(
                            state.items,
                            key = { it.id }
                        ) { item ->
                            CatalogCard(
                                item = item,
                                iconResId = item.iconResId,
                                schemaLabel = item.schemaLabel,
                                title = item.title ?: "",
                                onOpen = {
                                    navController.navigate(
                                        CatalogRoutes.catalogItem(item.id)
                                    )
                                },
                                onEdit = {
                                    navController.navigate(
                                        CatalogRoutes.catalogEditItem(item.id)
                                    )
                                },
                                onDriveBackup = {
                                    viewModel.backupCatalogItemToSheets(item.id)
                                },
                                onDelete = {
                                    viewModel.requestDelete(item)
                                }
                            )
                        }
                    }
                }
            }
        }

        /* ───────────── FILTER SHEET ───────────── */
        if (showFilterSheet) {
            MeadowBottomSheet(
                title = stringResource(CoreUiR.string.action_filter),
                onDismiss = { showFilterSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    CatalogFilterContent(
                        types = state.filterOptions.types,
                        selected = state.filters.types,
                        onChange = { newTypes ->
                            viewModel.updateFilters(
                                state.filters.copy(types = newTypes)
                            )
                        },
                        onClear = {
                            viewModel.clearTypeFilters()
                            showFilterSheet = false
                        }
                    )
                }
            }
        }

        /* ───────────── SORT SHEET ───────────── */
        if (showSortSheet) {
            MeadowBottomSheet(
                title = stringResource(CoreUiR.string.action_sort),
                onDismiss = { showSortSheet = false }
            ) {
                CatalogSortContent(
                    currentSort = state.sortMode,
                    onSortChange = {
                        viewModel.updateSort(it)
                        showSortSheet = false
                    }
                )
            }
        }

        /* ───────────── PROJECT PICKER ───────────── */
        if (showProjectPicker) {
            ProjectSelectorDialog(
                items = viewModel.projectPickerItems.collectAsState().value,
                selectedId = (state.scope as? CatalogListScope.Project)?.projectId,
                onSelect = {
                    viewModel.setScope(
                        it?.let { id -> CatalogListScope.Project(id) }
                            ?: CatalogListScope.Global
                    )
                },
                onDismiss = { showProjectPicker = false }
            )
        }

        /* ───────────── SERIES PICKER ───────────── */
        if (showSeriesPicker) {
            SeriesSelectorDialog(
                items = viewModel.seriesPickerItems.collectAsState().value,
                selectedId = (state.scope as? CatalogListScope.Series)?.seriesId,
                onSelect = {
                    it?.let { id ->
                        viewModel.setScope(CatalogListScope.Series(id))
                    }
                },
                onDismiss = { showSeriesPicker = false }
            )
        }

        state.pendingDeleteId?.let { id ->
            val title = state.allItems.firstOrNull { it.id == id }?.title ?: ""

            CatalogDeleteConfirmDialog(
                catalogTitle = title,
                onConfirmDelete = { viewModel.confirmDelete() },
                onDismiss = { viewModel.cancelDelete() }
            )
        }
    }
}
/* ─── EMPTY: NO CATALOG ITEMS EXIST ───────────────── */

@Composable
private fun EmptyNoCatalogItemsCard(
    showCreateButton: Boolean,
    onCreate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.catalog_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (showCreateButton) {
            Button(onClick = onCreate) {
                Text(stringResource(R.string.action_create_catalog_item))
            }
        }
    }
}

/* ─── EMPTY: NO CATALOG ITEM SEARCH RESULTS ───────────────── */

@Composable
private fun EmptyResultsCard(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}