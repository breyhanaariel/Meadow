package com.meadow.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meadow.app.R
import com.meadow.app.datastore.FeaturePreferences
import com.meadow.app.datastore.FeaturePreferencesKeys
import com.meadow.core.ai.R as CoreAiR
import com.meadow.core.common.settings.SettingsCategory
import com.meadow.core.common.settings.SettingsRegistry
import com.meadow.core.google.R as CoreGoogleR
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.CollapsibleSection
import com.meadow.core.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    val featurePrefs = remember { FeaturePreferences(context) }
    val featureStates by featurePrefs.features.collectAsState(initial = emptyMap())

    SettingsRegistry.ensureAllInjectorsLoaded()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = stringResource(R.string.menu_settings),
                style = MaterialTheme.typography.headlineLarge
            )


            Divider()
            /* ─── FEATURE SETTINGS ─────────────────── */
            CollapsibleSection(
                title = stringResource(CoreUiR.string.features)
            ) {
                FeaturePreferencesKeys.ALL.forEach { key ->
                    val enabled = featureStates[key] ?: false

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(FeaturePreferencesKeys.labelFor(key)),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Switch(
                            checked = enabled,
                            onCheckedChange = { checked ->
                                scope.launch {
                                    featurePrefs.setEnabled(key, checked)
                                }
                            }
                        )
                    }
                }
            }

            /* ─── THEME SETTINGS ─────────────────── */
            CollapsibleSection(
                title = stringResource(CoreUiR.string.theme_settings_title)
            ) {
                SettingsRegistry
                    .forCategory(SettingsCategory.THEME)
                    .forEach { it.Content() }
            }

            /* ─── GOOGLE SETTINGS ─────────────────── */
            CollapsibleSection(
                title = stringResource(CoreGoogleR.string.google_settings_title)
            ) {
                SettingsRegistry
                    .forCategory(SettingsCategory.GOOGLE)
                    .forEach { it.Content() }
            }
            /* ─── AI SETTINGS ─────────────────── */
            CollapsibleSection(
                title = stringResource(CoreAiR.string.ai_settings_title)
            ) {
                SettingsRegistry
                    .forCategory(SettingsCategory.AI)
                    .forEach { it.Content() }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ─── BACK TO HOME ────────────────── */
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_home))
            }
        }
    }
}
