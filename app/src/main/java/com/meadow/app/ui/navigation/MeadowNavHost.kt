package com.meadow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meadow.app.ui.screens.*

/**
 * Central navigation graph for the app.
 * Handles routing between Home, Project Dashboard, and other screens.
 */
@Composable
fun MeadowNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("project/{projectId}") { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            ProjectDashboardScreen(navController, projectId)
        }
        composable("script/{projectId}/{scriptId}") { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            val scriptId = backStackEntry.arguments?.getString("scriptId") ?: return@composable
            ScriptEditorScreen(navController, projectId, scriptId)
        }
        composable("catalog/{projectId}") { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            CatalogScreen(navController, projectId)
        }
        composable("calendar/{projectId}") { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            CalendarScreen(navController, projectId)
        }
        composable("settings") { SettingsScreen(navController) }
    }
}