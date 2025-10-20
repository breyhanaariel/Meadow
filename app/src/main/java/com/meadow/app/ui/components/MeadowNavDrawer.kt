package com.meadow.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meadow.app.R
import com.meadow.app.ui.viewmodel.ProjectViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun MeadowNavDrawer(
    expanded: Boolean,
    onClose: () -> Unit,
    navController: NavController,
    projectVM: ProjectViewModel = hiltViewModel()
) {
    if (!expanded) return

    val projects by projectVM.projects.collectAsState(initial = emptyList())
    var selectedProject by remember { mutableStateOf<String?>(null) }

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF3E8FF), Color(0xFFEAD9FF))
                ),
                RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                "Meadow",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF8B6EC9)
            )

            // 🌼 Project Selector
            Spacer(Modifier.height(8.dp))
            if (projects.isNotEmpty()) {
                var expandedProjects by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(onClick = { expandedProjects = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedProject ?: "Select Project")
                    }

                    DropdownMenu(
                        expanded = expandedProjects,
                        onDismissRequest = { expandedProjects = false }
                    ) {
                        projects.forEach { project ->
                            DropdownMenuItem(
                                text = { Text(project.name) },
                                onClick = {
                                    selectedProject = project.name
                                    expandedProjects = false
                                    projectVM.selectProject(project.id)
                                    navController.navigate("project/${project.id}")
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                if (selectedProject != null) {
                    Text(
                        text = "Current: $selectedProject",
                        color = Color(0xFF9B7BCE),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // ✨ Drawer Items
            DrawerItem(
                label = "Home",
                icon = ImageVector.vectorResource(R.drawable.ic_home),
                onClick = { navController.navigate("home") }
            )
            DrawerItem(
                label = "Project Dashboard",
                icon = ImageVector.vectorResource(R.drawable.ic_inventory),
                onClick = {
                    selectedProject?.let {
                        navController.navigate("project/${projectVM.getProjectIdByName(it)}")
                    }
                }
            )
            DrawerItem(
                label = "Scripts",
                icon = ImageVector.vectorResource(R.drawable.ic_script),
                onClick = {
                    selectedProject?.let {
                        navController.navigate("scripts/${projectVM.getProjectIdByName(it)}")
                    }
                }
            )
            DrawerItem(
                label = "Catalog",
                icon = ImageVector.vectorResource(R.drawable.ic_palette),
                onClick = {
                    selectedProject?.let {
                        navController.navigate("catalog/${projectVM.getProjectIdByName(it)}")
                    }
                }
            )
            DrawerItem(
                label = "Wiki",
                icon = ImageVector.vectorResource(R.drawable.ic_settings),
                onClick = {
                    selectedProject?.let {
                        navController.navigate("wiki/${projectVM.getProjectIdByName(it)}")
                    }
                }
            )
            DrawerItem(
                label = "Calendar",
                icon = ImageVector.vectorResource(R.drawable.ic_sync),
                onClick = {
                    selectedProject?.let {
                        navController.navigate("calendar/${projectVM.getProjectIdByName(it)}")
                    }
                }
            )
            DrawerItem(
                label = "Settings",
                icon = ImageVector.vectorResource(R.drawable.ic_settings),
                onClick = { navController.navigate("settings") }
            )

            Spacer(Modifier.weight(1f))
            Divider(color = Color(0xFFDAB0FF))
            TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DrawerItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    var sparkle by remember { mutableStateOf(false) }

    // Soft pastel glitter animation
    val alpha by rememberInfiniteTransition(label = "sparkle").animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                sparkle = true
                onClick()
            }
            .background(
                if (sparkle)
                    Brush.horizontalGradient(listOf(Color(0xFFF8E1FF), Color(0xFFEAD9FF)))
                else
                    Color.Transparent
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF9C7FFF),
            modifier = Modifier
                .size(20.dp)
                .alpha(alpha)
        )
        Text(label, color = Color(0xFF8B6EC9))
    }

    LaunchedEffect(sparkle) {
        if (sparkle) {
            delay(600)
            sparkle = false
        }
    }
}
