package com.meadow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.meadow.app.ui.MeadowNavHost
import com.meadow.app.ui.theme.MeadowTheme
import com.meadow.app.ui.components.MeadowNavDrawer
import com.meadow.app.ui.viewmodel.SettingsViewModel
import com.meadow.app.data.datastore.SettingsDataStore
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ds = SettingsDataStore(this)
        val settingsVM by viewModels<SettingsViewModel> { SettingsViewModel.Factory(ds) }

        setContent { // This is the opening brace
            val themeIndex by settingsVM.themeIndex.collectAsState()
            val lastProjectId by settingsVM.settings.lastProjectId.collectAsState(initial = null)
            val navController = rememberNavController()

            MeadowTheme(themeIndex) {
                var initialized by remember { mutableStateOf(false) }

                // Launch navigation when last project known
                LaunchedEffect(lastProjectId) {
                    if (!initialized) {
                        if (lastProjectId != null) {
                            navController.navigate("project/$lastProjectId")
                        } else {
                            navController.navigate("home")
                        }
                        initialized = true
                    }
                }

                // Rest of your UI setup (drawer + host)
                MeadowNavHost(navController)
            }
        } // <-- This closing brace was missing
    } // This closes onCreate
} // This closes MainActivity
