package com.meadow.app.ui.shell

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.meadow.app.ui.navigation.AppRoutes
import com.meadow.app.ui.navigation.MeadowNavHost
import com.meadow.core.ui.theme.MeadowTheme
import com.meadow.core.ui.theme.ThemeViewModel
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import kotlinx.coroutines.launch

@Composable
fun MeadowAppShell(
    activity: Activity,
    featureEntries: Set<@JvmSuppressWildcards FeatureEntry>,
    featureSpecs: Set<ProjectFeatureSpec>
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val appShellState = remember { AppShellState() }

    MeadowTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {}
        ) {
            Scaffold { innerPadding: PaddingValues ->
                Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MeadowNavHost(
                        navController = navController,
                        themeViewModel = ThemeViewModel(),
                        featureContextState = FeatureContextState(),
                        featureEntries = featureEntries
                    )
                }
            }
        }
    }
}