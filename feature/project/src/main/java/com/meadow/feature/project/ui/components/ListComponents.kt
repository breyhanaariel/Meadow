package com.meadow.feature.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meadow.core.ai.R as CoreAiR
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.*
import com.meadow.feature.project.R
import com.meadow.feature.project.domain.model.*
import com.meadow.feature.project.ui.state.*
import com.meadow.feature.project.ui.util.readCoverImage
import com.meadow.feature.project.ui.util.readTitle

/* ─── SERIES DROPDOWN ───────────────────── */
@Composable
fun SeriesDropdown(
    series: List<Series>,
    selectedId: String?,
    onSelect: (String?) -> Unit,
    onCreate: (String) -> Unit,
    onRename: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var creating by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var renameTitle by remember { mutableStateOf("") }
    val selected = series.firstOrNull { it.id == selectedId }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.series),
            style = MaterialTheme.typography.labelMedium
        )
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = true }
        ) {
            Text(selected?.title ?: stringResource(R.string.no_series))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.no_series)) },
                onClick = {
                    onSelect(null)
                    expanded = false
                }
            )
            series.forEach {
                DropdownMenuItem(
                    text = { Text(it.title) },
                    onClick = {
                        onSelect(it.id)
                        expanded = false
                    }
                )
            }
            Divider()
            if (!creating) {
                DropdownMenuItem(
                    text = { Text("＋ ${stringResource(R.string.create_new_series)}") },
                    onClick = {
                        creating = true
                        newTitle = ""
                    }
                )
            } else {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        placeholder = {
                            Text(stringResource(R.string.series_title))
                        }
                    )
                    Button(
                        enabled = newTitle.isNotBlank(),
                        onClick = {
                            onCreate(newTitle.trim())
                            expanded = false
                            creating = false
                        }
                    ) {
                        Text(stringResource(R.string.action_create_series))
                    }
                }
            }
            if (selected != null) {
                Divider()
                if (!renaming) {
                    DropdownMenuItem(
                        text = {
                            Text("✏ ${stringResource(R.string.rename_series)}")
                        },
                        onClick = {
                            renaming = true
                            renameTitle = selected.title
                        }
                    )
                } else {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = renameTitle,
                            onValueChange = { renameTitle = it }
                        )
                        Button(
                            enabled = renameTitle.isNotBlank(),
                            onClick = {
                                onRename(selected.id, renameTitle.trim())
                                expanded = false
                                renaming = false
                            }
                        ) {
                            Text(stringResource(CoreUiR.string.action_confirm_rename))
                        }
                    }
                }
            }
        }
    }
}

/* ─── SERIES CARD ───────────────────── */
@Composable
fun SeriesCard(
    series: Series,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onEditAiContext: () -> Unit,
    onDelete: () -> Unit
) {
    MeadowCard(
        modifier = Modifier.fillMaxWidth(),
        type = MeadowCardType.Content
    ) {
        Column {
            MeadowCardHeader(
                title = series.title,
                modifier = Modifier.clickable(onClick = onOpen)
            )
            Text(
                text = stringResource(
                    R.string.series_project_count,
                    series.projectIds.size
                ),
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            MeadowCardFooter {
                IconButton(onClick = onEditAiContext) {
                    Icon(
                        painterResource(CoreUiR.drawable.ic_ai),
                        stringResource(CoreAiR.string.ai_edit_context),
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = onRename) {
                    Icon(
                        painterResource(CoreUiR.drawable.ic_edit),
                        stringResource(CoreUiR.string.action_rename),
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painterResource(CoreUiR.drawable.ic_delete),
                        stringResource(CoreUiR.string.action_delete),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

/* ─── SERIES CARD GRID ───────────────────── */
@Composable
fun SeriesCardGrid(
    series: Series,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onEditAiContext: () -> Unit,
    onDelete: () -> Unit
) {
    MeadowCardGrid(
        modifier = Modifier.padding(4.dp),
        aspectRatio = 1f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onOpen)
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = .9f),
                            RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    IconButton(onClick = onEditAiContext) {
                        Icon(
                            painterResource(CoreUiR.drawable.ic_ai),
                            stringResource(CoreAiR.string.ai_edit_context),
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = onRename) {
                        Icon(
                            painterResource(CoreUiR.drawable.ic_edit),
                            stringResource(CoreUiR.string.action_rename),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}

/* ─── SYNC DOT ───────────────────── */
@Composable
private fun FirestoreSyncDot(
    syncUi: ProjectSyncUiState,
    modifier: Modifier = Modifier
) {
    val color = when (syncUi.status) {
        ProjectSyncStatus.IDLE -> MaterialTheme.colorScheme.secondary
        ProjectSyncStatus.SYNCING -> MaterialTheme.colorScheme.primary
        ProjectSyncStatus.ERROR -> MaterialTheme.colorScheme.error
    }
    Box(modifier = modifier.size(10.dp).clip(CircleShape).background(color))
}


/* ─── PROJECT CARD ───────────────────── */

@Composable
fun ProjectCard(
    project: Project,
    firestoreSyncUi: ProjectSyncUiState,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onFirestoreSync: () -> Unit,
    onDriveBackup: () -> Unit,
    onArchive: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    MeadowCard(
        modifier = Modifier.fillMaxWidth(),
        type = MeadowCardType.Content
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable(onClick = onOpen)
            ) {
                val cover = project.readCoverImage()
                if (cover != null) {
                    AsyncImage(
                        model = cover,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.project_no_cover),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                FirestoreSyncDot(
                    firestoreSyncUi,
                    Modifier.align(Alignment.TopEnd).padding(8.dp)
                )
            }
            Text(
                project.readTitle(),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
            )
            MeadowCardFooter {
                IconButton(onClick = onEdit) {
                    Icon(
                        painterResource(CoreUiR.drawable.ic_edit),
                        stringResource(CoreUiR.string.action_edit),
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painterResource(CoreUiR.drawable.ic_delete),
                        stringResource(CoreUiR.string.action_delete),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

/* ─── PROJECT GRID CARD ───────────────────── */
@Composable
fun ProjectCardGrid(
    project: Project,
    firestoreSyncUi: ProjectSyncUiState,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    MeadowCardGrid(
        modifier = Modifier.padding(4.dp),
        aspectRatio = 1f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onOpen)
        ) {
            val cover = project.readCoverImage()
            if (cover != null) {

                AsyncImage(
                    model = cover,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.project_no_cover),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

/* ─── VIEW MODE TOGGLE ───────────────────── */
@Composable
fun ListViewModeToggle(
    viewMode: ListViewMode,
    onChange: (ListViewMode) -> Unit
) {
    Row {
        IconButton(onClick = { onChange(ListViewMode.LIST) }) {
            Icon(
                Icons.Default.ViewAgenda,
                contentDescription = "List"
            )
        }
        IconButton(onClick = { onChange(ListViewMode.GRID) }) {
            Icon(
                Icons.Default.ViewModule,
                contentDescription = "Grid"
            )
        }
    }
}

/* ─── SEARCH TOOLBAR ───────────────────── */
@Composable
fun ListSearchToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    mode: ListSearchMode,
    viewMode: ListViewMode,
    onViewModeChange: (ListViewMode) -> Unit,
    onSortClick: () -> Unit,
    onVisibilityClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MeadowSearchField(
            value = query,
            onValueChange = onQueryChange
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (mode != ListSearchMode.SERIES) {
                    MeadowChip(
                        text = stringResource(CoreUiR.string.action_visibility),
                        type = MeadowChipType.Filter,
                        selected = false,
                        onToggle = { onVisibilityClick() }
                    )
                    MeadowChip(
                        text = stringResource(CoreUiR.string.action_filter),
                        type = MeadowChipType.Filter,
                        selected = false,
                        onToggle = { onFilterClick() }
                    )
                }
                MeadowChip(
                    text = stringResource(CoreUiR.string.action_sort),
                    type = MeadowChipType.Filter,
                    selected = false,
                    onToggle = { onSortClick() }
                )
            }
            ListViewModeToggle(viewMode, onViewModeChange)
        }
    }
}

/* ─── SORT + FILTER PANELS ───────────────────── */
@Composable
fun ProjectSortContent(
    currentSort: ProjectListSort,
    onSortChange: (ProjectListSort) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProjectListSort.values().forEach { sort ->
            MeadowChip(
                text = stringResource(sort.labelRes),
                type = MeadowChipType.Filter,
                selected = currentSort == sort,
                onToggle = { onSortChange(sort) }
            )
        }
    }
}

@Composable
fun SeriesSortContent(
    currentSort: SeriesListSort,
    onSortChange: (SeriesListSort) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SeriesListSort.values().forEach { sort ->
            MeadowChip(
                text = stringResource(sort.labelRes),
                type = MeadowChipType.Filter,
                selected = currentSort == sort,
                onToggle = { onSortChange(sort) }
            )
        }
    }
}

@Composable
fun ProjectVisibilityContent(
    state: ProjectListVisibility,
    onChange: (ProjectListVisibility) -> Unit,
    onClear: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        MeadowChip(
            text = stringResource(CoreUiR.string.action_show_archived),
            type = MeadowChipType.Filter,
            selected = state.showArchived,
            onToggle = {
                onChange(state.copy(showArchived = it))
            }
        )
        MeadowChip(
            text = stringResource(CoreUiR.string.action_show_completed),
            type = MeadowChipType.Filter,
            selected = state.showCompleted,
            onToggle = {
                onChange(state.copy(showCompleted = it))
            }
        )
        TextButton(onClick = onClear) {
            Text(stringResource(CoreUiR.string.action_clear_filters))
        }
    }
}

@Composable
fun ProjectFilterContent(
    state: ProjectListFilters,
    options: ProjectListFilterOptions,
    onChange: (ProjectListFilters) -> Unit,
    onClear: () -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        FilterGroup(
            title = stringResource(R.string.field_project_type),
            options = options.projectTypes,
            selected = state.projectTypes
        ) {
            onChange(state.copy(projectTypes = it))
        }

        FilterGroup(
            title = stringResource(R.string.field_project_audience),
            options = options.audiences,
            selected = state.audiences
        ) {
            onChange(state.copy(audiences = it))
        }

        FilterGroup(
            title = stringResource(R.string.field_project_genre),
            options = options.genres,
            selected = state.genres
        ) {
            onChange(state.copy(genres = it))
        }

        FilterGroup(
            title = stringResource(R.string.field_project_elements),
            options = options.elements,
            selected = state.elements
        ) {
            onChange(state.copy(elements = it))
        }

        FilterGroup(
            title = stringResource(R.string.field_project_rating),
            options = options.ratings,
            selected = state.ratings
        ) {
            onChange(state.copy(ratings = it))
        }

        FilterGroup(
            title = stringResource(R.string.field_project_warnings),
            options = options.warnings,
            selected = state.warnings
        ) {
            onChange(state.copy(warnings = it))
        }

        FilterGroup(
            title = stringResource(R.string.field_project_status),
            options = options.statuses,
            selected = state.statuses
        ) {
            onChange(state.copy(statuses = it))
        }

        FilterGroup(
            title = stringResource(R.string.field_project_format),
            options = options.formats,
            selected = state.formats
        ) {
            onChange(state.copy(formats = it))
        }

        MeadowChip(
            text = stringResource(CoreUiR.string.action_clear_filters),
            type = MeadowChipType.Filter,
            selected = false,
            onToggle = { onClear() }
        )
    }
}

@Composable
private fun FilterGroup(
    title: String,
    options: List<String>,
    selected: Set<String>,
    onToggle: (Set<String>) -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(title)

        options.forEach { option ->

            val isSelected = option in selected

            MeadowChip(
                text = option,
                type = MeadowChipType.Filter,
                selected = isSelected,
                onToggle = {
                    val newSet =
                        if (isSelected) selected - option
                        else selected + option

                    onToggle(newSet)
                }
            )
        }
    }
}