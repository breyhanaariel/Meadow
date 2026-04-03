package com.meadow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meadow.app.ui.screens.home.HomeScreen
import com.meadow.app.ui.screens.settings.SettingsScreen
import com.meadow.app.ui.screens.splash.SplashScreen
import com.meadow.core.ui.theme.ThemeViewModel
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.common.state.FeatureContextState

@Composable
fun MeadowNavHost(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    featureContextState: FeatureContextState,
    featureEntries: Set<@JvmSuppressWildcards FeatureEntry>
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // ─── App-level screens ───
        composable("splash") {
            SplashScreen(navController)
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                themeViewModel = themeViewModel
            )
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                themeViewModel = themeViewModel
            )
        }

        // ─── Feature graphs ───
        featureEntries.forEach { entry ->
            entry.registerGraph(this, navController)
        }
    }
}
