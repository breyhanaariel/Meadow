package com.meadow.feature.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.ui.components.MeadowButtonType
import com.meadow.core.ui.components.MeadowDialog
import com.meadow.core.ui.components.MeadowDialogScaffold
import com.meadow.core.ui.components.MeadowDialogType
import com.meadow.core.ui.components.MeadowInputDialog
import com.meadow.feature.project.R as R
import com.meadow.feature.project.api.SeriesSelectorItem
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.Series
import com.meadow.feature.project.ui.util.readTitle

/* ─── DELETE SERIES CONFIRMATION ───────────────────── */
/* Confirms permanent deletion of a series */

@Composable
fun SeriesDeleteConfirmDialog(
    seriesTitle: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialog(
        type = MeadowDialogType.Alert,
        title = stringResource(R.string.delete_series_title),
        message = stringResource(
            R.string.delete_series_message,
            seriesTitle
        ),
        onConfirm = onConfirmDelete,
        onDismiss = onDismiss
    )
}

/* ─── RENAME SERIES DIALOG ───────────────────── */
/* Allows renaming an existing series with duplicate validation */

@Composable
fun RenameSeriesDialog(
    title: String,
    initial: String,
    isDuplicate: (String) -> Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }

    val trimmed = text.trim()
    val duplicate =
        trimmed.isNotBlank() &&
                isDuplicate(trimmed) &&
                trimmed != initial.trim()

    MeadowInputDialog(
        title = title,
        value = text,
        onValueChange = { text = it },
        label = stringResource(R.string.series_name),
        error = if (duplicate)
            stringResource(R.string.series_name_duplicate)
        else null,
        confirmEnabled = trimmed.isNotBlank() && !duplicate,
        onConfirm = { onConfirm(trimmed) },
        onDismiss = onDismiss
    )
}

/* ─── CREATE SERIES DIALOG ───────────────────── */
/* Inline creation of a new series (used when none exist) */

@Composable
fun CreateSeriesDialog(
    isDuplicate: (String) -> Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    val trimmed = text.trim()
    val duplicate =
        trimmed.isNotBlank() && isDuplicate(trimmed)

    MeadowInputDialog(
        title = stringResource(R.string.action_create_series),
        value = text,
        onValueChange = { text = it },
        label = stringResource(R.string.series_name),
        error = if (duplicate)
            stringResource(R.string.series_name_duplicate)
        else null,
        confirmEnabled = trimmed.isNotBlank() && !duplicate,
        onConfirm = { onConfirm(trimmed) },
        onDismiss = onDismiss
    )
}

/* ─── CREATE OR SELECT SERIES DIALOG ───────────────────── */
/* Calm, focused dialog: select first, create if needed */

@Composable
fun CreateOrSelectSeriesDialog(
    series: List<Series>,
    selectedId: String?,
    isDuplicate: (String) -> Boolean,
    onSelect: (String?) -> Unit,
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreateSeriesDialog(
            isDuplicate = isDuplicate,
            onConfirm = { name ->
                onCreate(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
        return
    }

    MeadowDialogScaffold(
        title = {
            Text(stringResource(R.string.choose_series))
        },
        onDismiss = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                /* ─── SERIES SELECTOR (CUTE ROUNDED) ─── */
                SeriesSelectorDropdown(
                    series = series,
                    selectedId = selectedId,
                    onSelect = onSelect
                )

                /* ─── SUBTLE SPACING ─── */
                Spacer(Modifier.height(4.dp))

                /* ─── CREATE INLINE ACTION ─── */
                Text(
                    text = stringResource(R.string.action_create_series),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 4.dp)
                        .clickable { showCreateDialog = true },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MeadowButton(
                    text = stringResource(CoreUiR.string.action_confirm),
                    enabled = selectedId != null,
                    onClick = onConfirm
                )

                MeadowButton(
                    text = stringResource(CoreUiR.string.action_cancel),
                    type = MeadowButtonType.Ghost,
                    onClick = onDismiss
                )
            }
        }
    )
}

/* ─── SERIES SELECTOR DIALOG ───────────────────── */
/* Lightweight selector used when creation is not allowed */

@Composable
fun SeriesSelectorDialog(
    items: List<SeriesSelectorItem>,
    selectedId: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialogScaffold(
        onDismiss = onDismiss,
        title = {
            Text(stringResource(R.string.choose_series))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                /* ─── No series option ───────────────────── */
                Surface(
                    onClick = {
                        onSelect(null)
                        onDismiss()
                    },
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = if (selectedId == null) 2.dp else 0.dp,
                    color = if (selectedId == null)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.no_series),
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                /* ─── Existing series options ───────────────────── */
                items.forEach { series ->
                    val selected = series.id == selectedId

                    Surface(
                        onClick = {
                            onSelect(series.id)
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = if (selected) 2.dp else 0.dp,
                        color = if (selected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val displayTitle = series.title
                            ?: stringResource(com.meadow.core.ui.R.string.untitled)

                        Text(
                            text = displayTitle,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 14.dp
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}
@Composable
private fun SeriesSelectorDropdown(
    series: List<Series>,
    selectedId: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selected = series.firstOrNull { it.id == selectedId }

    Column {
        Surface(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.title ?: stringResource(R.string.no_series),
                    style = MaterialTheme.typography.bodyLarge
                )

                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.no_series)) },
                onClick = {
                    expanded = false
                    onSelect(null)
                }
            )

            series.forEach { s ->
                DropdownMenuItem(
                    text = { Text(s.title) },
                    onClick = {
                        expanded = false
                        onSelect(s.id)
                    }
                )
            }
        }
    }
}

/* ─── ADD AND REMOVE PROJECTS FROM SERIES DIALOG ───────────────────── */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditSeriesProjectsDialog(
    seriesTitle: String,
    projectsInSeries: List<Project>,
    availableProjects: List<Project>,
    projectSearchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddProject: (String) -> Unit,
    onRemoveProject: (String) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialogScaffold(
        title = { Text(seriesTitle) },
        onDismiss = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                /* ─── SEARCH (ADD ONLY) ─── */
                OutlinedTextField(
                    value = projectSearchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.search_projects))
                    },
                    singleLine = true
                )

                /* ─── PROJECTS IN THIS SERIES ─── */
                Text(
                    text = stringResource(R.string.projects_in_series),
                    style = MaterialTheme.typography.titleSmall
                )

                if (projectsInSeries.isEmpty()) {
                    Text(
                        text = stringResource(R.string.series_no_projects),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        projectsInSeries.forEach { project ->
                            InputChip(
                                selected = true,
                                onClick = { onRemoveProject(project.id) },
                                label = { Text(project.readTitle()) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }

                Divider()

                /* ─── ADD PROJECTS ─── */
                Text(
                    text = stringResource(R.string.series_add_projects),
                    style = MaterialTheme.typography.titleSmall
                )

                val filteredAvailable =
                    availableProjects.filter {
                        projectSearchQuery.isBlank() ||
                                it.readTitle()
                                    .contains(projectSearchQuery, ignoreCase = true)
                    }

                if (filteredAvailable.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_projects_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredAvailable.forEach { project ->
                            AssistChip(
                                onClick = { onAddProject(project.id) },
                                label = { Text(project.readTitle()) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            MeadowButton(
                text = stringResource(CoreUiR.string.action_done),
                onClick = onDismiss
            )
        }
    )
}