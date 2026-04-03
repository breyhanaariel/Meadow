package com.meadow.feature.catalog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.meadow.feature.catalog.ui.components.CatalogDeleteConfirmDialog
import com.meadow.feature.catalog.ui.components.CatalogFormContent
import com.meadow.feature.catalog.ui.viewmodel.EditCatalogItemViewModel
import com.meadow.feature.common.ai.*
import com.meadow.feature.common.state.FeatureContextState

@Composable
fun EditCatalogItemScreen(
    navController: NavController,
    itemId: String,
    viewModel: EditCatalogItemViewModel = hiltViewModel(),
    featureContextState: FeatureContextState = hiltViewModel()
) {
    /* ─── INITIAL LOAD ───────────────────────── */
    LaunchedEffect(itemId) {
        viewModel.load(itemId)
    }

    /* ─── UI STATE ──────────────────────────── */
    val state by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showUnsavedConfirm by remember { mutableStateOf(false) }
    var activeHelper by remember { mutableStateOf<FieldHelperSpec?>(null) }

    /* ─── PROJECT STATE ──────────────────────────── */
    LaunchedEffect(state.projectId) {
        state.projectId?.let { projectId ->
            featureContextState.setContext(
                featureContextState.context.value.copy(
                    projectId = projectId,
                    seriesId = state.seriesId,
                    catalogItemId = state.catalogItemId
                )
            )
        }
    }
    /* ─── AI UI STATE ───────────────────────── */
    val aiChatViewModel: AiChatViewModel = hiltViewModel()
    var showAiPanel by remember { mutableStateOf(false) }
    var showAiContextEditor by remember { mutableStateOf(false) }
    val itemTitle: String =
        state.fields.firstOrNull { it.definition.key == "title" }
            ?.value?.rawValue?.toString()
            ?: stringResource(R.string.catalog_item)

    val scopeKey: String? = remember(state.projectId, state.catalogItemId) {
        when {
            state.projectId.isNullOrBlank() -> AiScopeKeys.GLOBAL
            state.catalogItemId.isNullOrBlank() -> AiScopeKeys.project(state.projectId)
            else -> AiScopeKeys.catalog(state.projectId, state.catalogItemId)
        }
    }

    /* ─── SNACKBAR SYSTEM ───────────────────── */
    val snackbarHostState = remember { SnackbarHostState() }
    CollectUiMessages(viewModel.uiMessages, snackbarHostState)

    /* ─── AI FIELD HELPER CONTROLLER ─────────── */

    val aiFieldHelper = rememberAiFieldHelperController(
        scope = AiFieldHelperScope(
            projectId = state.projectId,
            seriesId = state.seriesId,
            catalogItemId = state.catalogItemId,
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
                projectId = state.projectId,
                seriesId = state.seriesId,
                scopeKey = scopeKey
            )

            aiChatViewModel.setContext(payload, itemTitle)
            showAiPanel = true
        },
        onOpenContextEditor = { showAiContextEditor = true }
    )

    /* ─── AI CONTEXT EDITOR  ─────────────────────── */
    if (showAiContextEditor) {
        AiContextEditor(
            scopeKey = scopeKey,
            onDismiss = { showAiContextEditor = false }
        )
    }

    /* ─── DELETE CONFIRM ────────────────────── */
    if (showDeleteConfirm) {
        CatalogDeleteConfirmDialog(
            catalogTitle = state.catalogItemId ?: "",
            onConfirmDelete = {
                showDeleteConfirm = false
                viewModel.delete()
                navController.popBackStack()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    /* ─── UNSAVED CONFIRM ───────────────────── */
    if (showUnsavedConfirm) {
        AlertDialog(
            onDismissRequest = { showUnsavedConfirm = false },
            title = { Text(stringResource(CoreUiR.string.unsaved_changes)) },
            text = { Text(stringResource(CoreUiR.string.unsaved_changes_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedConfirm = false
                    navController.popBackStack()
                }) {
                    Text(stringResource(CoreUiR.string.action_discard))
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedConfirm = false }) {
                    Text(stringResource(CoreUiR.string.action_keep_editing))
                }
            }
        )
    }

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
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                return@Scaffold
            }

            CatalogFormContent(
                fields = state.fields,
                seriesId = state.seriesId,
                seriesSharedFieldIds = state.seriesSharedFieldIds,
                referenceDataProvider = viewModel.referenceDataProvider,
                onFieldChange = viewModel::updateField,
                onToggleSeriesField = viewModel::toggleSeriesField,
                onShowHelper = { activeHelper = it },
                primaryButtonText = stringResource(CoreUiR.string.action_save),
                onPrimaryAction = viewModel::save,
                secondaryButtonText = stringResource(CoreUiR.string.action_cancel),
                onSecondaryAction = {
                    if (state.hasUnsavedChanges && !state.isSaving) {
                        showUnsavedConfirm = true
                    } else {
                        navController.popBackStack()
                    }
                },
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
