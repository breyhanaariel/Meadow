package com.meadow.feature.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.ui.components.MeadowButtonType
import com.meadow.core.ui.components.MeadowDialog
import com.meadow.core.ui.components.MeadowDialogScaffold
import com.meadow.core.ui.components.MeadowDialogType
import com.meadow.feature.project.R as R
import com.meadow.feature.project.api.ProjectSelectorItem
import com.meadow.feature.project.domain.model.ProjectType
import com.meadow.feature.project.domain.model.Series
import com.meadow.feature.project.ui.viewmodel.DashboardViewModel

/* ─── DELETE CONFIRMATION ───────────────────── */
@Composable
fun ProjectDeleteConfirmDialog(
    projectTitle: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialog(
        type = MeadowDialogType.Alert,
        title = stringResource(R.string.delete_project_title),
        message = stringResource(
            R.string.delete_project_message,
            projectTitle
        ),
        onConfirm = onConfirmDelete,
        onDismiss = onDismiss
    )
}


/* ─── PROJECT TYPE SELECTION ───────────────────── */
@Composable
fun ProjectTypeDialog(
    onSelect: (ProjectType) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialogScaffold(
        onDismiss = onDismiss,
        title = {
            Text(stringResource(R.string.choose_project_type))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProjectType.values()
                    .filter { it != ProjectType.UNKNOWN }
                    .forEach { type ->
                        val ui = type.toUiModel()
                        ProjectTypeOption(
                            label = ui.label,
                            icon = ui.icon
                        ) {
                            onSelect(type)
                        }
                    }
            }
        },
        dismissButton = {
            MeadowButton(
                text = stringResource(CoreUiR.string.action_cancel),
                type = MeadowButtonType.Ghost,
                onClick = onDismiss
            )
        }
    )
}

@Composable
private fun ProjectTypeOption(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/* ─── INTERNAL UI MAPPING ───────────────────── */

private data class ProjectTypeUi(
    val label: String,
    val icon: ImageVector
)

@Composable
private fun ProjectType.toUiModel(): ProjectTypeUi =
    when (this) {
        ProjectType.COMIC -> ProjectTypeUi(
            label = stringResource(R.string.project_type_comic),
            icon = Icons.Outlined.AutoStories
        )
        ProjectType.NOVEL -> ProjectTypeUi(
            label = stringResource(R.string.project_type_novel),
            icon = Icons.Outlined.MenuBook
        )
        ProjectType.MOVIE -> ProjectTypeUi(
            label = stringResource(R.string.project_type_movie),
            icon = Icons.Outlined.Movie
        )
        ProjectType.TV_SHOW -> ProjectTypeUi(
            label = stringResource(R.string.project_type_tv_show),
            icon = Icons.Outlined.Tv
        )
        ProjectType.GAME -> ProjectTypeUi(
            label = stringResource(R.string.project_type_game),
            icon = Icons.Outlined.SportsEsports
        )
        ProjectType.UNKNOWN -> ProjectTypeUi(
            label = stringResource(R.string.project_type_unknown),
            icon = Icons.Outlined.HelpOutline
        )
    }

/* ─── PROJECT SELECTOR ───────────────────── */

@Composable
fun ProjectSelectorDialog(
    items: List<ProjectSelectorItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialogScaffold(
        onDismiss = onDismiss,
        title = {
            Text(stringResource(R.string.choose_project))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEach { project ->
                    val selected = project.id == selectedId

                    Surface(
                        onClick = {
                            onSelect(project.id)
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
                        val displayTitle = project.title
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
