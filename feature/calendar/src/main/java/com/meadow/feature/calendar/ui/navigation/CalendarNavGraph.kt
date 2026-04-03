package com.meadow.feature.calendar.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.meadow.feature.calendar.ui.screens.CalendarEventScreen
import com.meadow.feature.calendar.ui.screens.CalendarScreen
import com.meadow.feature.calendar.ui.viewmodel.CalendarEventViewModel
import com.meadow.feature.calendar.ui.viewmodel.CalendarViewModel
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.BindFeatureContext

fun NavGraphBuilder.registerCalendarNavGraph(
    navController: NavController,
    featureContextState: FeatureContextState
) {

    /* ─── CALENDAR HOME (GLOBAL) ───────────────────── */
    composable(CalendarRoutes.CALENDAR_HOME) {

        val vm: CalendarViewModel = hiltViewModel()

        featureContextState.BindFeatureContext(null)

        CalendarScreen(
            navController = navController,
            featureContextState = featureContextState,
            viewModel = vm,
            projectId = null
        )
    }

    /* ─── CALENDAR HOME (PROJECT) ───────────────────── */
    composable(
        route = CalendarRoutes.CALENDAR_HOME_PROJECT,
        arguments = listOf(
            navArgument(CalendarRoutes.ArgProjectId) {
                type = NavType.StringType
            }
        )
    ) { entry ->

        val projectId =
            requireNotNull(
                entry.arguments?.getString(CalendarRoutes.ArgProjectId)
            )

        val vm: CalendarViewModel = hiltViewModel(entry)

        featureContextState.BindFeatureContext(projectId = projectId)

        CalendarScreen(
            navController = navController,
            featureContextState = featureContextState,
            viewModel = vm,
            projectId = projectId
        )
    }

    /* ─── CREATE EVENT ───────────────────── */
    composable(
        route = CalendarRoutes.CALENDAR_CREATE,
        arguments = listOf(
            navArgument(CalendarRoutes.ArgProjectId) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { entry ->

        val projectId =
            entry.arguments?.getString(CalendarRoutes.ArgProjectId)

        val vm: CalendarEventViewModel = hiltViewModel(entry)

        featureContextState.BindFeatureContext(projectId = projectId)

        CalendarEventScreen(
            navController = navController,
            viewModel = vm,
            localId = null,
            initialProjectId = projectId
        )
    }

    /* ─── EDIT EVENT ───────────────────── */
    composable(
        route = CalendarRoutes.CALENDAR_EDIT,
        arguments = listOf(
            navArgument(CalendarRoutes.ArgLocalId) {
                type = NavType.StringType
            }
        )
    ) { entry ->

        val localId =
            requireNotNull(
                entry.arguments?.getString(CalendarRoutes.ArgLocalId)
            )

        val vm: CalendarEventViewModel = hiltViewModel(entry)

        featureContextState.BindFeatureContext()

        CalendarEventScreen(
            navController = navController,
            viewModel = vm,
            localId = localId,
            initialProjectId = null
        )
    }
}