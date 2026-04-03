package com.meadow.feature.project.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.meadow.feature.common.api.FeatureContext
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.project.ui.screens.*

fun NavGraphBuilder.registerProjectNavGraph(
    navController: NavController,
    featureContextState: FeatureContextState
) {

    /* ─── PROJECT LIST ───────────────────── */

    composable(ProjectRoutes.PROJECT_LIST) {
        LaunchedEffect(Unit) {
            featureContextState.clearProject()
        }

        ProjectListScreen(
            navController = navController,
            featureContextState = featureContextState
        )
    }

    /* ─── PROJECT DASHBOARD ───────────────────── */
    composable(
        route = ProjectRoutes.PROJECT_DASHBOARD,
        arguments = listOf(
            navArgument(ProjectRoutes.ArgProjectId) {
                type = NavType.StringType
            }
        )
    ) { entry ->
        val projectId = requireNotNull(
            entry.arguments?.getString(ProjectRoutes.ArgProjectId)
        )

        LaunchedEffect(projectId) {
            featureContextState.setContext(
                FeatureContext(projectId = projectId)
            )
        }

        DashboardScreen(
            navController = navController,
            featureContextState = featureContextState
        )
    }

    /* ─── CREATE PROJECT ───────────────────── */
    composable(ProjectRoutes.PROJECT_CREATE) {
        CreateProjectScreen(
            navController = navController
        )
    }

    /* ─── EDIT PROJECT ───────────────────── */
    composable(
        route = ProjectRoutes.PROJECT_EDIT,
        arguments = listOf(
            navArgument(ProjectRoutes.ArgProjectId) {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val viewModel: EditProjectViewModel =
            hiltViewModel(backStackEntry)

        EditProjectScreen(
            navController = navController,
            featureContextState = featureContextState,
            viewModel = viewModel
        )
    }

    /* ─── PROJECT HISTORY ───────────────────── */
    composable(
        route = ProjectRoutes.PROJECT_HISTORY,
        arguments = listOf(
            navArgument(ProjectRoutes.ArgProjectId) {
                type = NavType.StringType
            }
        )
    ) { entry ->
        val projectId = requireNotNull(
            entry.arguments?.getString(ProjectRoutes.ArgProjectId)
        )

        LaunchedEffect(projectId) {
            featureContextState.setContext(
                FeatureContext(projectId = projectId)
            )
        }

        ProjectHistoryScreen(
            navController = navController,
            featureContextState = featureContextState
        )
    }

    /* ─── SERIES LIST ───────────────────── */
            composable(ProjectRoutes.SERIES_LIST) {
                LaunchedEffect(Unit) {
                    featureContextState.clearProject()
                }

                SeriesListScreen(
                    navController = navController,
                    featureContextState = featureContextState
                )
            }
}
