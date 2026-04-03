package com.meadow.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.meadow.app.R
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.R as ProjectR
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.ui.navigation.ProjectRoutes
import com.meadow.feature.project.ui.state.ProjectFeatureUi
import com.meadow.feature.project.ui.util.readCoverImage
import com.meadow.feature.project.ui.util.readTitle

@Composable
fun DrawerMenu(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    selectedProject: Project?,
    hasProjects: Boolean,
    projectCount: Int,
    projectFeatures: List<ProjectFeatureUi>,
    onToggleFeature: (Preferences.Key<Boolean>, Boolean) -> Unit,
    onRequestProjectPicker: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* ─── HEADER STATES ───────────────────── */

            when {
                selectedProject != null -> {
                    SelectedProjectHeader(
                        project = selectedProject,
                        navController = navController,
                        onCloseDrawer = onCloseDrawer
                    )
                }

                hasProjects -> {
                    ChooseProjectButton {
                        onRequestProjectPicker()
                        onCloseDrawer()
                    }
                }


                else -> {
                    CreateProjectButton {
                        navController.navigate(ProjectRoutes.projectCreate())
                        onCloseDrawer()
                    }
                }
            }

            /* ─── SELECTED PROJECT ───────────────────── */
            if (selectedProject != null) {

                FeatureGrid(
                    features = projectFeatures,
                    modifier = Modifier.weight(1f),
                    onToggleFeature = onToggleFeature,
                    onOpenFeature = { feature ->

                        if (!feature.enabled) return@FeatureGrid

                        navController.navigate(feature.route)

                        onCloseDrawer()
                    }
                )

                Spacer(Modifier.height(12.dp))

                if (projectCount > 1) {
                    OutlinedButton(
                        onClick = {
                            onRequestProjectPicker()
                            onCloseDrawer()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(ProjectR.string.switch_project))
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedProjectHeader(
    project: Project,
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    navController.navigate(ProjectRoutes.projectDashboard(project.id))
                    onCloseDrawer()
                },
            contentAlignment = Alignment.Center
        ) {
            project.readCoverImage()?.let { cover ->
                AsyncImage(
                    model = cover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text(
            text = project.readTitle(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(ProjectRoutes.projectDashboard(project.id))
                    onCloseDrawer()
                }
        )
    }
}

@Composable
private fun FeatureGrid(
    features: List<ProjectFeatureUi>,
    onToggleFeature: (Preferences.Key<Boolean>, Boolean) -> Unit,
    onOpenFeature: (ProjectFeatureUi) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {

        items(features) { feature ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                IconButton(
                    onClick = { onOpenFeature(feature) }
                ) {
                    feature.icon()
                }

                Text(
                    text = stringResource(feature.titleRes),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                /* ─── ITEM COUNT ───────────────────── */

                if (feature.count > 0) {
                    Text(
                        text = feature.count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Switch(
                    checked = feature.enabled,
                    onCheckedChange = { enabled ->
                        onToggleFeature(feature.key, enabled)
                    }
                )
            }
        }
    }
}
@Composable
private fun ChooseProjectButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(ProjectR.string.choose_project))
    }
}

@Composable
private fun CreateProjectButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(ProjectR.string.action_create_project))
    }
}
