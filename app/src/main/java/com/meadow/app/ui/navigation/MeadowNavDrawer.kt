package com.meadow.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meadow.R

/**
 * MeadowNavDrawer.kt
 *
 * A floating top-left navigation button that expands into
 * a side drawer for switching between screens.
 */

@Composable
fun MeadowNavDrawer(
    navController: NavController,
    currentProjectName: String? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        // Floating circular button in top-left
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }

        if (isExpanded) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(260.dp)
                    .padding(top = 70.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Text(
                        text = currentProjectName ?: "Meadow",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Divider()

                    // Navigation Items
                    DrawerItem("Home", Icons.Default.Home) {
                        navController.navigate("home"); isExpanded = false
                    }

                    if (currentProjectName != null) {
                        DrawerItem("Dashboard", Icons.Default.Dashboard) {
                            navController.navigate("project/${currentProjectName}")
                            isExpanded = false
                        }
                        DrawerItem("Catalog", Icons.Default.List) {
                            navController.navigate("catalog/${currentProjectName}")
                            isExpanded = false
                        }
                        DrawerItem("Scripts", Icons.Default.Description) {
                            navController.navigate("script/${currentProjectName}")
                            isExpanded = false
                        }
                        DrawerItem("Calendar", Icons.Default.CalendarMonth) {
                            navController.navigate("calendar/${currentProjectName}")
                            isExpanded = false
                        }
                    }

                    Divider()
                    DrawerItem("Settings", Icons.Default.Settings) {
                        navController.navigate("settings"); isExpanded = false
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label)
        Spacer(modifier = Modifier.width(12.dp))
        Text(label)
    }
}
