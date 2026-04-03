package com.meadow.feature.catalog.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.meadow.feature.catalog.ui.screens.*
import com.meadow.feature.catalog.ui.util.CatalogSchemaBootstrap
import com.meadow.feature.catalog.ui.util.CatalogSchemaUiResolver
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.BindFeatureContext
import com.meadow.feature.common.ui.screens.FeatureDisabledScreen
import com.meadow.feature.project.data.preferences.ProjectFeaturePreferences
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProjectRepoEntryPoint {
    fun projectRepository(): ProjectRepositoryContract
}
@EntryPoint
@InstallIn(SingletonComponent::class)
interface CatalogSchemaUiResolverEntryPoint {
    fun catalogSchemaUiResolver(): CatalogSchemaUiResolver
}


fun NavGraphBuilder.registerCatalogNavGraph(
    navController: NavHostController,
    featureContextState: FeatureContextState,
    schemaBootstrap: CatalogSchemaBootstrap
) {
    schemaBootstrap.ensureInitialized()

    /* ─── GLOBAL CATALOG ───────────────────── */
    composable(CatalogRoutes.GLOBAL_CATALOG) {
        featureContextState.BindFeatureContext()
        CatalogListScreen(
            navController = navController,
            featureContextState = featureContextState
        )
    }

    /* ─── PROJECT CATALOG ───────────────────── */
    composable(
        route = CatalogRoutes.PROJECT_CATALOG,
        arguments = listOf(
            navArgument(CatalogRoutes.ArgProjectId) {
                type = NavType.StringType
            }
        )
    ) { entry ->

        val context = LocalContext.current
        val projectId =
            entry.arguments?.getString(CatalogRoutes.ArgProjectId)

        if (projectId == null) {
            FeatureDisabledScreen(
                featureName = "Catalog",
                onBack = { navController.popBackStack() }
            )
            return@composable
        }

        val projectRepo = remember {
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                ProjectRepoEntryPoint::class.java
            ).projectRepository()
        }

        val projectState by projectRepo
            .observeProject(projectId)
            .collectAsState(initial = null)

        val project = projectState
        if (project == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@composable
        }

        val projectType = project.type

        val prefs = remember(projectId, projectType) {
            ProjectFeaturePreferences(
                context = context,
                projectId = projectId,
                projectType = projectType
            )
        }

        val featureStates by prefs.features.collectAsState(initial = emptyMap())

        val catalogKey = remember(projectId) {
            booleanPreferencesKey("project_${projectId}_catalog")
        }

        val catalogEnabled = featureStates[catalogKey] == true

        if (!catalogEnabled) {
            FeatureDisabledScreen(
                featureName = "Catalog",
                onBack = { navController.popBackStack() }
            )
            return@composable
        }

        featureContextState.BindFeatureContext(projectId = projectId)

        CatalogListScreen(
            navController = navController,
            featureContextState = featureContextState
        )
    }

    /* ─── SERIES CATALOG ───────────────────── */
    composable(
        route = CatalogRoutes.SERIES_CATALOG,
        arguments = listOf(
            navArgument(CatalogRoutes.ArgSeriesId) {
                 type = NavType.StringType
            }
        )
    ) { entry ->

        val context = LocalContext.current
        val seriesId =
            entry.arguments?.getString(CatalogRoutes.ArgSeriesId)

        if (seriesId == null) {
            FeatureDisabledScreen(
                featureName = "Catalog",
                onBack = { navController.popBackStack() }
            )
            return@composable
        }

        featureContextState.BindFeatureContext(seriesId = seriesId)

        CatalogListScreen(
            navController = navController,
            featureContextState = featureContextState
        )
    }

    /* ─── CATALOG ITEM ───────────────────── */
    composable(
        route = CatalogRoutes.CATALOG_ITEM,
        arguments = listOf(
            navArgument(CatalogRoutes.ArgItemId) {
                type = NavType.StringType
            }
        )
    ) { entry ->
        val itemId =
            requireNotNull(entry.arguments?.getString(CatalogRoutes.ArgItemId))

        CatalogItemScreen(
            navController = navController,
            itemId = itemId
        )
    }

    /* ─── CREATE CATALOG ITEM ───────────────────── */
    composable(
        route = "${CatalogRoutes.CATALOG_ITEM_CREATE}?projectId={projectId}&seriesId={seriesId}",
        arguments = listOf(
            navArgument("projectId") { type = NavType.StringType },
            navArgument("seriesId") {
                type = NavType.StringType
                nullable = true
            }
        )
    ) { entry ->

        val projectId =
            requireNotNull(entry.arguments?.getString("projectId"))

        val seriesId =
            entry.arguments?.getString("seriesId")

        featureContextState.BindFeatureContext(projectId = projectId, seriesId = seriesId)

        CreateCatalogItemScreen(
            navController = navController,
            projectId = projectId,
            seriesId = seriesId
        )
    }

    /* ─── EDIT CATALOG ITEM ───────────────────── */
    composable(
        route = CatalogRoutes.CATALOG_ITEM_EDIT,
        arguments = listOf(
            navArgument(CatalogRoutes.ArgItemId) {
                type = NavType.StringType
            }
        )
    ) { entry ->

        val itemId =
            requireNotNull(entry.arguments?.getString(CatalogRoutes.ArgItemId))

        EditCatalogItemScreen(
            navController = navController,
            itemId = itemId
        )
    }
}
