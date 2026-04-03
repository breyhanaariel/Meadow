package com.meadow.feature.script.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.meadow.feature.common.api.FeatureContext
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.BindFeatureContext
import com.meadow.feature.script.ui.screens.ScriptEditorScreen
import com.meadow.feature.script.ui.screens.ScriptListScreen
import com.meadow.feature.script.ui.viewmodel.ScriptEditorViewModel
import com.meadow.feature.script.ui.viewmodel.ScriptListViewModel

fun NavGraphBuilder.registerScriptNavGraph(
    navController: NavController,
    featureContextState: FeatureContextState
) {
    /* ─── SCRIPT LIST ───────────────────── */
    composable(
        route = ScriptRoutes.SCRIPT_LIST_PROJECT,
        arguments = listOf(
            navArgument(ScriptRoutes.ArgProjectId) { type = NavType.StringType }
        )
    ) { entry ->
        val projectId = requireNotNull(entry.arguments?.getString(ScriptRoutes.ArgProjectId))
        val vm: ScriptListViewModel = hiltViewModel(entry)
        featureContextState.BindFeatureContext(projectId = projectId)
        ScriptListScreen(
            navController = navController,
            viewModel = vm,
            projectId = projectId
        )
    }
    /* ─── SCRIPT EDITOR ───────────────────── */
    composable(
        route = ScriptRoutes.SCRIPT_EDITOR,
        arguments = listOf(
            navArgument(ScriptRoutes.ArgProjectId) { type = NavType.StringType },
            navArgument(ScriptRoutes.ArgScriptId) { type = NavType.StringType },
            navArgument(ScriptRoutes.ArgVariantId) { type = NavType.StringType }
        )
    ) { entry ->

        val projectId = requireNotNull(
            entry.arguments?.getString(ScriptRoutes.ArgProjectId)
        )

        val scriptId = requireNotNull(
            entry.arguments?.getString(ScriptRoutes.ArgScriptId)
        )

        val variantId = requireNotNull(
            entry.arguments?.getString(ScriptRoutes.ArgVariantId)
        )

        val vm: ScriptEditorViewModel = hiltViewModel(entry)

        vm.bind(
            scriptId = scriptId,
            variantId = variantId
        )

        featureContextState.BindFeatureContext(projectId = projectId, scriptId = scriptId)

        ScriptEditorScreen(
            navController = navController,
            viewModel = vm
        )
    }
}
