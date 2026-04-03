package com.meadow.feature.project.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.R as R
import com.meadow.feature.project.domain.model.ProjectType

data class ProjectTypeUi(
    val label: String,
    val icon: ImageVector
)

@Composable
fun ProjectType.toUi(): ProjectTypeUi =
    ProjectTypeUi(
        label = stringResource(labelRes),
        icon = when (this) {
            ProjectType.NOVEL -> Icons.Outlined.MenuBook
            ProjectType.COMIC -> Icons.Outlined.AutoStories
            ProjectType.MOVIE -> Icons.Outlined.Movie
            ProjectType.TV_SHOW -> Icons.Outlined.LiveTv
            ProjectType.GAME -> Icons.Outlined.SportsEsports
            ProjectType.UNKNOWN -> Icons.Outlined.HelpOutline
        }
    )
