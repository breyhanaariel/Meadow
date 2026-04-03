package com.meadow.feature.catalog.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meadow.core.ai.domain.model.AiContextPayload
import com.meadow.core.ai.ui.components.AiChatPanel
import com.meadow.core.ai.ui.components.AiContextEditor
import com.meadow.core.ai.ui.components.AiSidePanel
import com.meadow.core.ai.viewmodel.AiChatViewModel
import com.meadow.core.data.fields.FieldHelperSpec
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowFieldHelper
import com.meadow.core.ui.events.CollectUiMessages
import com.meadow.feature.catalog.R
import com.meadow.feature.catalog.domain.model.CatalogType
import com.meadow.feature.catalog.ui.components.CatalogFormContent
import com.meadow.feature.catalog.ui.components.CatalogTypeDialog
import com.meadow.feature.catalog.ui.util.CatalogSchemaUiResolver
import com.meadow.feature.catalog.ui.util.CatalogSchemaUiResolverViewModel
import com.meadow.feature.catalog.ui.viewmodel.CreateCatalogItemEvent
import com.meadow.feature.catalog.ui.viewmodel.CreateCatalogItemViewModel
import com.meadow.feature.common.ai.*


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateCatalogItemScreen(
    navController: NavController,
    projectId: String,
    seriesId: String?,
    viewModel: CreateCatalogItemViewModel = hiltViewModel()
) {
    /* ─── INITIAL LOAD ───────────────────────── */
    LaunchedEffect(projectId, seriesId) {
        viewModel.initialize(projectId, seriesId)
    }

    /* ─── UI STATE ──────────────────────────── */
    val state by viewModel.uiState.collectAsState()
    val schemas by viewModel.schemas.collectAsState(emptyList())
    val availableTypes by viewModel.availableCatalogTypes.collectAsState()
    val referenceDataProvider = viewModel.referenceDataProvider
    var activeHelper by remember { mutableStateOf<FieldHelperSpec?>(null) }
    val schemaResolverVm: CatalogSchemaUiResolverViewModel = hiltViewModel()
    val schemaResolver = schemaResolverVm.resolver

    /* ─── AI UI STATE ───────────────────────── */
    val aiChatViewModel: AiChatViewModel = hiltViewModel()
    var showAiPanel by remember { mutableStateOf(false) }
    var showAiContextEditor by remember { mutableStateOf(false) }

    val catalogIdForScope: String? =
        state.fields.firstOrNull()?.value?.ownerItemId

    val itemTitle: String =
        state.fields.firstOrNull { it.definition.key == "title" }
            ?.value?.rawValue?.toString()
            ?: stringResource(R.string.catalog_item)

    val scopeKey: String? = remember(projectId, catalogIdForScope) {
        when {
            catalogIdForScope.isNullOrBlank() -> AiScopeKeys.project(projectId)
            else -> AiScopeKeys.catalog(projectId, catalogIdForScope)
        }
    }

    /* ─── SNACKBAR SYSTEM ───────────────────── */
    val snackbarHostState = remember { SnackbarHostState() }
    CollectUiMessages(viewModel.uiMessages, snackbarHostState)

    /* ─── NAVIGATION EVENTS ─────────────────── */
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is CreateCatalogItemEvent.NavigateBack) {
                navController.popBackStack()
            }
        }
    }


    /* ─── CATALOG TYPE DIALOG ───────────────────────── */
    val showCatalogTypeDialog =
        state.catalogType == null && availableTypes.isNotEmpty()

    if (showCatalogTypeDialog) {
        CatalogTypeDialog(
            availableTypes = availableTypes,
            selectedType = state.catalogType,
            schemas = schemas,
            resolver = schemaResolver,
            onTypeSelected = viewModel::selectCatalogType,
            onSchemaSelected = viewModel::selectSchema,
            onDismiss = { navController.popBackStack() }
        )

    }

    /* ─── AI FIELD HELPER CONTROLLER ─────────── */
    val aiFieldHelper = rememberAiFieldHelperController(
        scope = AiFieldHelperScope(
            projectId = projectId,
            seriesId = state.seriesId,
            catalogItemId = catalogIdForScope,
            scriptId = null,
            nodeId = null,
            itemTitle = itemTitle
        ),
        fields = state.fields,
        snackbarHostState = snackbarHostState,
        onApplyFieldValue = viewModel::updateFieldValue,
        onOpenChats = {
            val payload = AiContextPayload(
                summary = "",
                tone = "",
                themes = emptyList(),
                projectId = projectId,
                seriesId = state.seriesId,
                scopeKey = scopeKey
            )
            aiChatViewModel.setContext(payload, itemTitle)
            showAiPanel = true
        },
        onOpenContextEditor = { showAiContextEditor = true }
    )

    /* ─── AI CHAT PANEL ─────────────────────── */
    if (showAiPanel) {
        AiSidePanel(onClose = { showAiPanel = false }) {
            AiChatPanel(
                navController = navController,
                viewModel = aiChatViewModel,
                onDismiss = { showAiPanel = false },
                onClose = { showAiPanel = false }
            )
        }
    }

    /* ─── AI CONTEXT EDITOR  ─────────────────────── */
    if (showAiContextEditor) {
        AiContextEditor(
            scopeKey = scopeKey,
            onDismiss = { showAiContextEditor = false }
        )
    }

    /* ─── SCREEN ───────────────────────────── */
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AiFabMenu(
                visible = true,
                onAction = { action ->
                    when (action) {
                        AiFabAction.Generate -> aiFieldHelper.generate()
                        AiFabAction.Improve -> aiFieldHelper.improve()
                        AiFabAction.Rewrite -> aiFieldHelper.rewrite()
                        AiFabAction.Shorten -> aiFieldHelper.shorten()
                        AiFabAction.Expand -> aiFieldHelper.expand()
                        AiFabAction.OpenChats -> aiFieldHelper.openChats()
                        AiFabAction.ManageContext -> aiFieldHelper.openContext()
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.schemaId != null -> {
                    CatalogFormContent(
                        fields = state.fields,
                        seriesId = state.seriesId,
                        seriesSharedFieldIds = state.seriesSharedFieldIds,
                        referenceDataProvider = referenceDataProvider,
                        onFieldChange = viewModel::updateField,
                        onToggleSeriesField = viewModel::toggleSeriesField,
                        onShowHelper = { activeHelper = it },
                        primaryButtonText = stringResource(CoreUiR.string.action_save),
                        onPrimaryAction = viewModel::saveCatalogItem,
                        secondaryButtonText = stringResource(CoreUiR.string.action_cancel),
                        onSecondaryAction = { navController.popBackStack() },
                        enabled = !state.isSaving,
                        showPrimaryLoading = state.isSaving,
                        onFieldFocused = aiFieldHelper::onFieldFocused,
                        onFieldLongPress = aiFieldHelper::onFieldLongPress
                    )

                    MeadowFieldHelper(
                        helperSpec = activeHelper,
                        onDismiss = { activeHelper = null }
                    )
                }
            }
        }
    }
}
