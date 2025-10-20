package com.meadow.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.meadow.R

/**
 * Floating top-left sidebar navigation.
 * Collapsible — opens when user clicks the unicorn button.
 */
@Composable
fun MeadowSidebar(
    navController: NavHostController,
    currentProjectId: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        // Unicorn FAB to toggle sidebar
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = Color(0xFFEBE3FF),
            contentColor = Color.White
        ) {
            Icon(painterResource(R.drawable.ic_unicorn), contentDescription = "Menu")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFFFDF6FF))
        ) {
            DropdownMenuItem(
                text = { Text("🏠 Home") },
                onClick = {
                    navController.navigate("home")
                    expanded = false
                }
            )

            if (currentProjectId != null) {
                DropdownMenuItem(
                    text = { Text("📂 Project Dashboard") },
                    onClick = {
                        navController.navigate("project/$currentProjectId")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("📝 Script") },
                    onClick = {
                        navController.navigate("script/$currentProjectId/main")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("📚 Catalog") },
                    onClick = {
                        navController.navigate("catalog/$currentProjectId")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("📅 Calendar") },
                    onClick = {
                        navController.navigate("calendar/$currentProjectId")
                        expanded = false
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("⚙️ Settings") },
                onClick = {
                    navController.navigate("settings")
                    expanded = false
                }
            )
        }
    }
}