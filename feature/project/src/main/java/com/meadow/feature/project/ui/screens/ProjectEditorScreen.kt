package com.meadow.feature.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meadow.core.data.fields.FieldKind
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.ui.components.*
import com.meadow.feature.project.R
import com.meadow.feature.project.domain.model.Series
import com.meadow.feature.project.ui.components.AiAwareField
import com.meadow.feature.project.ui.components.AiFieldHelperController
import com.meadow.feature.project.ui.components.AiFieldHelperScope
import com.meadow.feature.project.ui.components.AiFieldHost
import com.meadow.feature.project.ui.components.SeriesDropdown
import com.meadow.feature.project.ui.state.CreateProjectUiState
import com.meadow.feature.project.ui.state.EditProjectUiState
import com.meadow.feature.project.ui.viewmodel.ProjectEditorViewModel

@Composable
fun ProjectEditorScreen(
    navController: NavController,
    projectId: String?,
    viewModel: ProjectEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val series by viewModel.series.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(projectId) {
        viewModel.load(projectId)
    }

    val title =
        when (state) {
            is CreateProjectUiState ->
                stringResource(R.string.action_create_project)

            is EditProjectUiState ->
                stringResource(R.string.action_edit_project)

            else -> ""
        }
    val showDelete = state is EditProjectUiState

    AiFieldHost(
        scope = AiFieldHelperScope(
            projectId = (state as? EditProjectUiState)?.projectId,
            seriesId = state.seriesId,
            catalogItemId = null,
            scriptId = null,
            nodeId = null,
            itemTitle = state.fields
                .firstOrNull { it.definition.key == "title" }
                ?.value?.rawValue?.toString()
                ?: ""
        ),
        fields = state.fields,
        snackbarHostState = snackbarHostState,
        onApplyFieldValue = viewModel::updateFieldValue,
        onOpenChats = { },
        onOpenContextEditor = { }
    ) { controller ->
        MeadowFormScaffold(
            title = title,
            showDelete = showDelete,
            onBack = { navController.popBackStack() },
            onSave = viewModel::save,
            onDelete = if (showDelete) viewModel::delete else null,
            saving = viewModel.isSaving(state)
        ) {
            ProjectFormContent(
                fields = state.fields,
                controller = controller,
                seriesId = state.seriesId,
                seriesSharedFieldIds = state.seriesSharedFieldIds,
                series = series,
                onSelectSeries = viewModel::updateSeries,
                onCreateSeriesInline = viewModel::createSeriesInline,
                onRenameSeries = viewModel::renameSeries,
                onFieldValueChange = viewModel::updateFieldValue,
                onToggleSeriesField = viewModel::toggleSeriesField,
                enabled = !viewModel.isSaving(state)
            )
        }
    }
}

@Composable
private fun ProjectFormContent(
    fields: List<FieldWithValue>,
    controller: AiFieldHelperController,
    seriesId: String?,
    seriesSharedFieldIds: Set<String>,
    series: List<Series>,
    onSelectSeries: (String?) -> Unit,
    onCreateSeriesInline: (String) -> Unit,
    onRenameSeries: (String, String) -> Unit,
    onFieldValueChange: (FieldValue) -> Unit,
    onToggleSeriesField: (String, Boolean) -> Unit,
    enabled: Boolean
) {
    val sortedFields = remember(fields) { fields.sortedBy { it.definition.order } }
    val canToggleSeriesFields = seriesId != null

    SeriesDropdown(
        series = series,
        selectedId = seriesId,
        onSelect = onSelectSeries,
        onCreate = onCreateSeriesInline,
        onRename = onRenameSeries
    )

    sortedFields.forEach { field ->
        val isSeriesField = canToggleSeriesFields && seriesSharedFieldIds.contains(field.definition.id)

        AiAwareField(
            field = field,
            controller = controller,
            onValueChange = { newValue ->
                if (enabled) onFieldValueChange(newValue)
            }
        )
        if (canToggleSeriesFields && field.definition.kind != FieldKind.BOOLEAN) {
            SeriesFieldToggle(
                isSeriesField = isSeriesField,
                onToggle = {
                    if (enabled) onToggleSeriesField(field.definition.id, it)
                }
            )
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SeriesFieldToggle(
    isSeriesField: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text =
                if (isSeriesField)
                    stringResource(R.string.series_field_scope_series)
                else
                    stringResource(R.string.series_field_scope_project),
            style = MaterialTheme.typography.labelSmall
        )
        Switch(
            checked = isSeriesField,
            onCheckedChange = onToggle
        )
    }
}