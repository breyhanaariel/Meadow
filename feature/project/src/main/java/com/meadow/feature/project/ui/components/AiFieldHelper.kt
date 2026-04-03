package com.meadow.feature.project.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.data.fields.isAiHelper
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowBottomSheet
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.ui.components.MeadowButtonType
import com.meadow.feature.project.R as R
import com.meadow.feature.project.aicontext.data.repository.ProjectContextRepository
import com.meadow.feature.project.aicontext.domain.AiMode
import com.meadow.feature.project.aicontext.domain.AiScopeKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.launch

data class AiFieldHelperScope(
    val projectId: String?,
    val seriesId: String?,
    val catalogItemId: String?,
    val scriptId: String?,
    val nodeId: String?,
    val itemTitle: String,
    val projectTypeKey: String? = null,
)

private fun resolveFieldLabel(
    context: Context,
    labelKey: String?
): String {
    if (labelKey.isNullOrBlank()) return ""

    val resId = context.resources.getIdentifier(
        labelKey,
        "string",
        context.packageName
    )

    return if (resId != 0) context.getString(resId) else labelKey
}

class AiFieldHelperController internal constructor(
    private val openForSingle: (FieldWithValue, AiMode) -> Unit,
    private val openPickerForMode: (AiMode) -> Unit,
    private val setFocused: (FieldWithValue) -> Unit,
    private val openChatsInternal: () -> Unit,
    private val openContextInternal: () -> Unit
) {
    fun onFieldFocused(field: FieldWithValue) = setFocused(field)

    fun onFieldLongPress(field: FieldWithValue) {
        if (!field.definition.isAiHelper()) return
        openForSingle(field, AiMode.GENERATE)
    }

    fun generate() = openPickerForMode(AiMode.GENERATE)
    fun improve() = openPickerForMode(AiMode.IMPROVE)
    fun rewrite() = openPickerForMode(AiMode.REWRITE)
    fun expand() = openPickerForMode(AiMode.EXPAND)
    fun shorten() = openPickerForMode(AiMode.SHORTEN)
    fun openChats() = openChatsInternal()
    fun openContext() = openContextInternal()
}

@Composable
fun rememberAiFieldHelperController(
    scope: AiFieldHelperScope,
    fields: List<FieldWithValue>,
    snackbarHostState: SnackbarHostState,
    onApplyFieldValue: (FieldValue) -> Unit,
    onOpenChats: () -> Unit,
    onOpenContextEditor: () -> Unit,
    viewModel: AiFieldHelperViewModel = hiltViewModel()
): AiFieldHelperController {
    var focusedFieldId by remember { mutableStateOf<String?>(null) }
    var showPreview by remember { mutableStateOf(false) }
    var previewPayload by remember { mutableStateOf<PreviewPayload?>(null) }
    var showFieldPicker by remember { mutableStateOf(false) }
    var pendingMode by remember { mutableStateOf<AiMode?>(null) }

    val aiHelperFields = remember(fields) {
        fields.filter { it.definition.isAiHelper() }
    }

    val undoMap = remember { mutableStateMapOf<String, String?>() }

    val appliedText = stringResource(R.string.ai_applied)
    val undoText = stringResource(CoreUiR.string.action_undo)
    val genericErrorText = stringResource(R.string.ai_failed_generic)

    fun setFocused(field: FieldWithValue) {
        focusedFieldId = field.definition.id
    }

    fun openForSingle(field: FieldWithValue, mode: AiMode) {
        setFocused(field)

        viewModel.runForSingleField(
            scope = scope,
            targetField = field,
            mode = mode,
            onSuccess = {
                previewPayload = it
                showPreview = true
            },
            onFailure = { error ->
                val message = error.takeIf { it.isNotBlank() } ?: genericErrorText
                viewModelScopeSnackbar(viewModel, snackbarHostState, message)
            }
        )
    }

    if (showPreview && previewPayload != null) {
        AiFieldPreviewSheet(
            payload = previewPayload!!,
            onDismiss = {
                showPreview = false
                previewPayload = null
            },
            onConfirm = { confirmed ->
                confirmed.forEach { item ->
                    val currentField = fields.firstOrNull { it.definition.id == item.fieldId }

                    val resolvedOwnerId =
                        currentField?.value?.ownerItemId
                            ?: scope.catalogItemId
                            ?: scope.nodeId
                            ?: scope.scriptId
                            ?: scope.projectId
                            ?: scope.seriesId
                            ?: ""

                    undoMap[item.fieldId] = currentField?.value?.rawValue?.toString()

                    onApplyFieldValue(
                        FieldValue(
                            fieldId = item.fieldId,
                            ownerItemId = resolvedOwnerId,
                            rawValue = item.after.orEmpty()
                        )
                    )
                }

                showPreview = false
                previewPayload = null

                viewModel.viewModelScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = appliedText,
                        actionLabel = undoText
                    )

                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                        confirmed.forEach { item ->
                            val currentField = fields.firstOrNull { it.definition.id == item.fieldId }

                            val resolvedOwnerId =
                                currentField?.value?.ownerItemId
                                    ?: scope.catalogItemId
                                    ?: scope.nodeId
                                    ?: scope.scriptId
                                    ?: scope.projectId
                                    ?: scope.seriesId
                                    ?: ""

                            onApplyFieldValue(
                                FieldValue(
                                    fieldId = item.fieldId,
                                    ownerItemId = resolvedOwnerId,
                                    rawValue = undoMap[item.fieldId].orEmpty()
                                )
                            )
                        }
                    }
                }
            }
        )
    }

    if (showFieldPicker && pendingMode != null) {
        AiFieldPickerSheet(
            mode = pendingMode!!,
            fields = aiHelperFields,
            onDismiss = {
                showFieldPicker = false
                pendingMode = null
            },
            onSelect = { field ->
                val mode = pendingMode ?: AiMode.GENERATE
                showFieldPicker = false
                pendingMode = null
                openForSingle(field, mode)
            }
        )
    }

    return remember(scope, fields) {
        AiFieldHelperController(
            openForSingle = ::openForSingle,
            openPickerForMode = { mode ->
                val focused = focusedFieldId?.let { id ->
                    fields.firstOrNull { it.definition.id == id }
                }

                when {
                    focused != null -> {
                        openForSingle(focused, mode)
                    }

                    aiHelperFields.isNotEmpty() -> {
                        pendingMode = mode
                        showFieldPicker = true
                    }

                    else -> {
                        viewModelScopeSnackbar(viewModel, snackbarHostState, genericErrorText)
                    }
                }
            },
            setFocused = ::setFocused,
            openChatsInternal = onOpenChats,
            openContextInternal = onOpenContextEditor
        )
    }
}

@HiltViewModel
class AiFieldHelperViewModel @Inject constructor(
    private val aiManager: AiManager,
    private val projectContextRepo: ProjectContextRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    fun runForSingleField(
        scope: AiFieldHelperScope,
        targetField: FieldWithValue,
        mode: AiMode,
        onSuccess: (PreviewPayload) -> Unit,
        onFailure: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            onSuccess(
                buildPreview(
                    scope = scope,
                    targets = listOf(targetField),
                    mode = mode
                )
            )
        } catch (t: Throwable) {
            onFailure(t.message.orEmpty())
        }
    }

    private suspend fun buildPreview(
        scope: AiFieldHelperScope,
        targets: List<FieldWithValue>,
        mode: AiMode
    ): PreviewPayload {
        val projectContextText = projectContextRepo.buildContextText(scope)
        val action = mode.toFieldAiAction()

        val items = targets.map {
            val scopeKey = resolveScopeKey(scope)

            val response = aiManager.bud.runFieldAction(
                currentText = it.value?.rawValue?.toString(),
                action = action,
                fieldLabel = resolveFieldLabel(
                    context = appContext,
                    labelKey = it.definition.labelKey
                ),
                scopeKey = scopeKey,
                projectTypeKey = scope.projectTypeKey,
                extraContextAdditions = projectContextText
            )

            PreviewItem(
                fieldId = it.definition.id,
                ownerItemId = it.value?.ownerItemId ?: "",
                fieldLabel = resolveFieldLabel(
                    context = appContext,
                    labelKey = it.definition.labelKey
                ),
                before = it.value?.rawValue?.toString(),
                after = response.content.trim()
            )
        }

        return PreviewPayload(
            id = UUID.randomUUID().toString(),
            mode = mode,
            scopeLabel = scope.itemTitle,
            items = items
        )
    }

    private fun resolveScopeKey(scope: AiFieldHelperScope): String {
        return when {
            scope.projectId != null && scope.nodeId != null -> {
                AiScopeKeys.node(scope.projectId, scope.nodeId)
            }

            scope.projectId != null && scope.scriptId != null -> {
                AiScopeKeys.script(scope.projectId, scope.scriptId)
            }

            scope.projectId != null && scope.catalogItemId != null -> {
                AiScopeKeys.catalog(scope.projectId, scope.catalogItemId)
            }

            scope.projectId != null -> {
                AiScopeKeys.project(scope.projectId)
            }

            scope.seriesId != null -> {
                AiScopeKeys.series(scope.seriesId)
            }

            else -> {
                AiScopeKeys.GLOBAL
            }
        }
    }
}

@Composable
private fun AiFieldPreviewSheet(
    payload: PreviewPayload,
    onDismiss: () -> Unit,
    onConfirm: (List<PreviewItem>) -> Unit
) {
    MeadowBottomSheet(
        title = stringResource(R.string.ai_preview_title),
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.ai_preview_scope,
                    payload.scopeLabel
                )
            )

            Divider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(payload.items, key = { it.fieldId }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.fieldLabel,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = stringResource(R.string.ai_preview_before),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = item.before
                                ?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.ai_preview_empty)
                        )

                        Divider()

                        Text(
                            text = stringResource(R.string.ai_preview_after),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = item.after
                                ?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.ai_preview_empty)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MeadowButton(
                    text = stringResource(CoreUiR.string.action_cancel),
                    type = MeadowButtonType.Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                )

                MeadowButton(
                    text = stringResource(CoreUiR.string.action_apply),
                    type = MeadowButtonType.Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onConfirm(payload.items) }
                )
            }
        }
    }
}

@Composable
private fun AiFieldPickerSheet(
    mode: AiMode,
    fields: List<FieldWithValue>,
    onSelect: (FieldWithValue) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowBottomSheet(
        title = stringResource(R.string.ai_select_field),
        onDismiss = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fields, key = { it.definition.id }) { field ->
                val context = LocalContext.current

                MeadowButton(
                    text = resolveFieldLabel(context, field.definition.labelKey),
                    type = MeadowButtonType.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSelect(field) }
                )
            }
        }
    }
}

private fun viewModelScopeSnackbar(
    viewModel: ViewModel,
    host: SnackbarHostState,
    message: String
) {
    viewModel.viewModelScope.launch {
        host.showSnackbar(message)
    }
}

data class PreviewPayload(
    val id: String,
    val mode: AiMode,
    val scopeLabel: String,
    val items: List<PreviewItem>
)

data class PreviewItem(
    val fieldId: String,
    val ownerItemId: String,
    val fieldLabel: String,
    val before: String?,
    val after: String?
)
