package com.meadow.app.ui.shell

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meadow.app.ui.navigation.AppRoutes
import com.meadow.app.ui.navigation.MeadowNavHost
import com.meadow.core.ai.R as CoreAiR
import com.meadow.core.ai.ui.screens.AiChatScreen
import com.meadow.core.ai.viewmodel.AiChatViewModel
import com.meadow.core.ui.locale.AppLanguage
import com.meadow.core.ui.locale.LanguageStore
import com.meadow.core.ui.locale.LocalizedContext
import com.meadow.core.ui.theme.MeadowTheme
import com.meadow.core.ui.theme.ThemeViewModel
import com.meadow.feature.common.api.FeatureContext
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.components.LanguagePickerSheet
import com.meadow.feature.common.ui.components.MeadowTopBar
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.project.api.ProjectSelectorItem
import com.meadow.feature.project.data.preferences.ProjectFeaturePreferences
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import com.meadow.feature.project.ui.components.DrawerMenu
import com.meadow.feature.project.ui.components.ProjectSelectorDialog
import com.meadow.feature.project.ui.navigation.ProjectRoutes
import com.meadow.feature.project.ui.shell.MainShellViewModel
import com.meadow.feature.project.ui.util.readTitle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeadowAppShell(
    activity: Activity,
    featureEntries: Set<@JvmSuppressWildcards FeatureEntry>,
    featureSpecs: Set<ProjectFeatureSpec>
) {
    val appShellState = remember { AppShellState() }
    val languageStore = remember { LanguageStore(activity) }
    val currentLanguage by languageStore.language.collectAsState(
        initial = AppLanguage.ENGLISH
    )

    MeadowTheme {
        MeadowSystemBars()

        val localizedContext = LocalContext.current
        CompositionLocalProvider(
            LocalizedContext provides localizedContext
        ) {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val featureContextState: FeatureContextState = hiltViewModel()
            val featureContext by featureContextState.context.collectAsState()
            val kebabActions by featureContextState.kebabActions.collectAsState()

            val shellViewModel: MainShellViewModel = hiltViewModel()
            val hasProjects by shellViewModel.hasProjects.collectAsState()
            val projectCount by shellViewModel.projectCount.collectAsState()

            val selectedProject by shellViewModel
                .selectedProject(featureContext.projectId)
                .collectAsState()

            val projectFeaturePrefs =
                selectedProject?.let { project ->
                    remember(project.id) {
                        ProjectFeaturePreferences(
                            context = activity,
                            projectId = project.id,
                            projectType = project.type
                        )
                    }
                }

            val featureStates =
                projectFeaturePrefs
                    ?.features
                    ?.collectAsState(initial = emptyMap())
                    ?.value
                    ?: emptyMap()

            val projectFeatures =
                remember(selectedProject?.id, featureStates, featureSpecs) {
                    val project = selectedProject ?: return@remember emptyList()

                    featureStates.mapNotNull { (key, enabled) ->
                        val featureKey =
                            key.name.removePrefix("project_${project.id}_")

                        val spec =
                            featureSpecs.firstOrNull { it.key == featureKey }
                                ?: return@mapNotNull null

                        com.meadow.feature.project.ui.state.ProjectFeatureUi(
                            key = key,
                            enabled = enabled,
                            titleRes = spec.titleRes,
                            icon = spec.icon,
                            route = spec.routeForProject(project.id)
                        )
                    }
                }

            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collect { entry ->
                    featureContextState.clearKebabActions()

                    val route = entry.destination.route
                    val projectId = entry.arguments?.getString("projectId")

                    if (route?.startsWith("project_") == true && projectId != null) {
                        featureContextState.setContext(
                            FeatureContext(projectId = projectId)
                        )
                    } else {
                        featureContextState.clearProject()
                    }
                }
            }

            val themeViewModel: ThemeViewModel = viewModel()
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route

            val aiChatViewModel: AiChatViewModel = hiltViewModel()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerMenu(
                        navController = navController,
                        onCloseDrawer = { scope.launch { drawerState.close() } },
                        selectedProject = selectedProject,
                        hasProjects = hasProjects,
                        projectCount = projectCount,
                        projectFeatures = projectFeatures,
                        onToggleFeature = { key, enabled ->
                            scope.launch {
                                projectFeaturePrefs?.setEnabled(key, enabled)
                            }
                        },
                        onRequestProjectPicker = {
                            appShellState.showProjectPicker()
                        }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        if (currentRoute != AppRoutes.SPLASH) {
                            MeadowTopBar(
                                onWordmarkClick = {
                                    navController.navigate(AppRoutes.HOME)
                                },
                                onMenuClick = {
                                    scope.launch { drawerState.open() }
                                },
                                onChatClick = {
                                    appShellState.openAiChat()
                                },
                                onSettingsClick = {
                                    navController.navigate(AppRoutes.SETTINGS)
                                },
                                kebabActions = kebabActions,
                                currentLanguage = currentLanguage,
                                onLanguageClick = {
                                    appShellState.showLanguagePicker()
                                }
                            )
                        }
                    }
                ) { innerPadding: PaddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MeadowNavHost(
                            navController = navController,
                            themeViewModel = themeViewModel,
                            featureContextState = featureContextState,
                            featureEntries = featureEntries
                        )

                        AnimatedVisibility(
                            visible = appShellState.isAiChatOpen,
                            enter = slideInHorizontally { it } + fadeIn(),
                            exit = slideOutHorizontally { it } + fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(380.dp)
                                    .align(Alignment.CenterEnd)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                IconButton(
                                    onClick = { appShellState.closeAiChat() },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(
                                            CoreAiR.string.cd_close_chat
                                        )
                                    )
                                }

                                AiChatScreen(
                                    viewModel = aiChatViewModel,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }

            if (appShellState.showLanguagePicker) {
                ModalBottomSheet(
                    onDismissRequest = { appShellState.hideLanguagePicker() }
                ) {
                    LanguagePickerSheet(
                        current = currentLanguage,
                        onSelect = { language ->
                            scope.launch {
                                appShellState.hideLanguagePicker()
                                languageStore.setLanguage(language)
                                activity.recreate()
                            }
                        }
                    )
                }
            }

            if (appShellState.showProjectPicker) {
                ProjectSelectorDialog(
                    items = shellViewModel.projects.collectAsState().value.map { project ->
                        ProjectSelectorItem(
                            id = project.id,
                            title = project.readTitle(),
                            typeKey = project.type.name
                        )
                    },
                    selectedId = selectedProject?.id,
                    onSelect = { projectId ->
                        shellViewModel.selectProject(projectId)
                        navController.navigate(
                            ProjectRoutes.projectDashboard(projectId)
                        )
                        appShellState.hideProjectPicker()
                    },
                    onDismiss = { appShellState.hideProjectPicker() }
                )
            }
        }
    }
}

@Composable
private fun MeadowSystemBars() {
    val view = LocalView.current
    val window = activityWindowOrNull() ?: return
    val statusBarColor = MaterialTheme.colorScheme.surfaceContainerHigh

    SideEffect {
        window.statusBarColor = statusBarColor.toArgb()

        val controller = WindowInsetsControllerCompat(window, view)
        controller.isAppearanceLightStatusBars = true
    }
}

@Composable
private fun activityWindowOrNull() =
    (LocalView.current.context as? Activity)?.window