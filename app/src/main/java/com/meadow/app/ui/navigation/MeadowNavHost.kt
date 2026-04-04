package com.meadow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meadow.app.ui.screens.home.HomeScreen
import com.meadow.app.ui.screens.settings.SettingsScreen
import com.meadow.app.ui.screens.splash.SplashScreen
import com.meadow.core.ui.theme.ThemeViewModel
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry

@Composable
fun MeadowNavHost(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    featureContextState: FeatureContextState,
    featureEntries: Set<@JvmSuppressWildcards FeatureEntry>
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {
        composable(AppRoutes.SPLASH) {
            SplashScreen(navController)
        }

        composable(AppRoutes.HOME) {
            HomeScreen(
                navController = navController,
                themeViewModel = themeViewModel
            )
        }

        composable(AppRoutes.SETTINGS) {
            SettingsScreen(
                navController = navController,
                themeViewModel = themeViewModel
            )
        }

        featureEntries.forEach { entry ->
            entry.registerGraph(this, navController)
        }
    }
}