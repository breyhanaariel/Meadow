package com.meadow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meadow.app.domain.models.Project
import com.meadow.app.ui.components.PastelTextField
import com.meadow.app.ui.components.PastelButton
import com.meadow.app.ui.theme.*
import com.meadow.app.viewmodel.ProjectViewModel

/**
 * ProjectDashboardScreen.kt
 *
 * Displays editable project details on the left (for large screens)
 * and quick-access cards (Scripts, Catalog, Calendar) on the right.
 * Supports both tablet and phone layouts using responsive layout.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDashboardScreen(
    projectId: String,
    projectViewModel: ProjectViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateToScripts: (String) -> Unit = {},
    onNavigateToCatalog: (String) -> Unit = {},
    onNavigateToCalendar: (String) -> Unit = {},
) {
    val project by projectViewModel.getProjectById(projectId).collectAsState(initial = null)

    if (project == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MeadowGradients.LavenderPink),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading your project meadow… 🌸",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
        return
    }

    var editableProject by remember { mutableStateOf(project!!) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = editableProject.title.ifEmpty { "Untitled Project" },
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MeadowGradients.LavenderPink)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🌸 Left Column — Editable Project Info
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .background(PastelPeach, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Project Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                }

                item {
                    PastelTextField(
                        value = editableProject.title,
                        onValueChange = { editableProject = editableProject.copy(title = it) },
                        placeholder = "Project Title"
                    )
                }

                item {
                    PastelTextField(
                        value = editableProject.pitch,
                        onValueChange = { editableProject = editableProject.copy(pitch = it) },
                        placeholder = "Project Pitch"
                    )
                }

                item {
                    PastelTextField(
                        value = editableProject.genre ?: "",
                        onValueChange = { editableProject = editableProject.copy(genre = it) },
                        placeholder = "Genre(s)"
                    )
                }

                item {
                    PastelTextField(
                        value = editableProject.status ?: "",
                        onValueChange = { editableProject = editableProject.copy(status = it) },
                        placeholder = "Project Status (Idea, Draft, Released...)"
                    )
                }

                item {
                    PastelButton(
                        text = "💾 Save Changes",
                        onClick = {
                            projectViewModel.updateProject(editableProject)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 🌼 Right Column — Quick Access Cards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuickAccessCard(
                    title = "Scripts",
                    description = "Write and edit your project scripts",
                    icon = Icons.Default.Description,
                    background = PastelLavender,
                    onClick = { onNavigateToScripts(projectId) }
                )

                QuickAccessCard(
                    title = "Catalog",
                    description = "View and manage your characters, worlds, and more",
                    icon = Icons.Default.Book,
                    background = PastelMint,
                    onClick = { onNavigateToCatalog(projectId) }
                )

                QuickAccessCard(
                    title = "Calendar",
                    description = "Plan your writing schedule and milestones",
                    icon = Icons.Default.CalendarToday,
                    background = PastelRose,
                    onClick = { onNavigateToCalendar(projectId) }
                )
            }
        }
    }
}

/**
 * Reusable Quick Access Card Component
 */
@Composable
fun QuickAccessCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = title, tint = TextPrimary)
            Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}