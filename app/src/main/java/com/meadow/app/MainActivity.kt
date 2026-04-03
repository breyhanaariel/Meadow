package com.meadow.app

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meadow.app.ui.components.DrawerMenu
import com.meadow.app.ui.components.LanguagePickerSheet
import com.meadow.app.ui.components.MeadowTopBar
import com.meadow.app.ui.navigation.MeadowNavHost
import com.meadow.app.ui.shell.MainShellViewModel
import com.meadow.core.ai.R as CoreAiR
import com.meadow.core.ai.ui.screens.AiChatScreen
import com.meadow.core.ai.viewmodel.AiChatViewModel
import com.meadow.core.ui.locale.AppLanguage
import com.meadow.core.ui.locale.LanguageStore
import com.meadow.core.ui.locale.LocaleManager
import com.meadow.core.ui.locale.LocalizedContext
import com.meadow.core.ui.theme.MeadowTheme
import com.meadow.core.ui.theme.ThemeViewModel
import com.meadow.feature.common.api.FeatureContext
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.project.api.ProjectSelectorItem
import com.meadow.feature.project.data.preferences.ProjectFeaturePreferences
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import com.meadow.feature.project.ui.components.ProjectSelectorDialog
import com.meadow.feature.project.ui.navigation.ProjectRoutes
import com.meadow.feature.project.ui.util.readTitle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProjectFeatureSpecEntryPoint {
        fun specs(): Set<ProjectFeatureSpec>
    }

    @Inject
    lateinit var featureEntries: Set<@JvmSuppressWildcards FeatureEntry>

    override fun attachBaseContext(newBase: Context) {
        val language = runBlocking {
            LanguageStore(newBase).language.first()
        }
        val localized = LocaleManager.apply(newBase, language)
        super.attachBaseContext(localized)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MainContent()
        }
    }

    @Composable
    private fun MeadowSystemBars() {
        val view = LocalView.current
        val window = (view.context as Activity).window
        val statusBarColor = MaterialTheme.colorScheme.surfaceContainerHigh

        SideEffect {
            window.statusBarColor = statusBarColor.toArgb()

            val controller = WindowInsetsControllerCompat(window, view)
            controller.isAppearanceLightStatusBars = true
        }
    }


    @Composable
    private fun MainContent() {
        val languageStore = remember { LanguageStore(this@MainActivity) }
        val currentLanguage by languageStore.language.collectAsState(
            initial = AppLanguage.ENGLISH
        )

        var showLanguagePicker by remember { mutableStateOf(false) }

        MeadowTheme {
            MeadowSystemBars()

            val localizedContext = LocalContext.current
            CompositionLocalProvider(
                LocalizedContext provides localizedContext
            ) {
                var isAiChatOpen by remember { mutableStateOf(false) }

                val navController = rememberNavController()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var showProjectPicker by remember { mutableStateOf(false) }

                val featureContextState: FeatureContextState = hiltViewModel()
                val featureContext by featureContextState.context.collectAsState()
                val kebabActions by featureContextState.kebabActions.collectAsState()

                val shellViewModel: MainShellViewModel = hiltViewModel()
                val hasProjects by shellViewModel.hasProjects.collectAsState()
                val projectCount by shellViewModel.projectCount.collectAsState()

                val selectedProject by shellViewModel
                    .selectedProject(featureContext.projectId)
                    .collectAsState()

                val featureSpecs = remember {
                    EntryPointAccessors.fromApplication(
                        this@MainActivity,
                        ProjectFeatureSpecEntryPoint::class.java
                    ).specs()
                }

                val projectFeaturePrefs =
                    selectedProject?.let {
                        remember(it.id) {
                            ProjectFeaturePreferences(
                                context = this@MainActivity,
                                projectId = it.id,
                                projectType = it.type
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
                            onRequestProjectPicker = { showProjectPicker = true }
                        )
                    }
                ) {
                    Scaffold(
                        topBar = {
                            if (currentRoute != "splash") {
                                MeadowTopBar(
                                    onWordmarkClick = { navController.navigate("home") },
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onChatClick = { isAiChatOpen = true },
                                    onSettingsClick = { navController.navigate("settings") },
                                    kebabActions = kebabActions,
                                    currentLanguage = currentLanguage,
                                    onLanguageClick = { showLanguagePicker = true }
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
                                visible = isAiChatOpen,
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
                                        onClick = { isAiChatOpen = false },
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

                /* ─── LANGUAGE PICKER ───────────────────── */
                if (showLanguagePicker) {
                    ModalBottomSheet(
                        onDismissRequest = { showLanguagePicker = false }
                    ) {
                        LanguagePickerSheet(
                            current = currentLanguage,
                            onSelect = { language ->
                                // FIX: write first, then recreate
                                scope.launch {
                                    showLanguagePicker = false
                                    languageStore.setLanguage(language)
                                    this@MainActivity.recreate()
                                }
                            }
                        )
                    }
                }

                /* ─── PROJECT PICKER ───────────────────── */
                if (showProjectPicker) {
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
                        },
                        onDismiss = { showProjectPicker = false }
                    )
                }
            }
        }
    }
}
