package com.meadow.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.meadow.app.R
import com.meadow.app.datastore.FeaturePreferences
import com.meadow.app.datastore.FeaturePreferencesKeys
import com.meadow.core.ui.theme.ThemeViewModel
import com.meadow.feature.project.R as ProjectR
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.ui.navigation.ProjectRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@Composable
fun HomeScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val featurePrefs = remember { FeaturePreferences(navController.context) }

    val featureStates by featurePrefs.features.collectAsState(initial = emptyMap())

    val projectFeatureEnabled =
        featureStates[FeaturePreferencesKeys.PROJECT] == true

    when {
        projectFeatureEnabled -> {
            ProjectHome(navController)
        }
        else -> {
            DefaultHome(
                onOpenSettings = {
                    navController.navigate("settings")
                }
            )
        }
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val projectRepository: ProjectRepositoryContract
) : ViewModel() {

    val hasAnyProject: StateFlow<Boolean> =
        projectRepository.observeProjects()
            .map { it.isNotEmpty() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )
}

@Composable
private fun DefaultHome(
    onOpenSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.home_open_settings))
            }
        }
    }
}

@Composable
private fun ProjectHome(
    navController: NavController
) {
    LaunchedEffect(Unit) {
        navController.navigate(ProjectRoutes.projectList()) {
            launchSingleTop = true
        }
    }
}
