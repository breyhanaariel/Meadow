package com.meadow.feature.script.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meadow.core.ui.events.CollectUiMessages
import com.meadow.feature.script.R
import com.meadow.feature.script.domain.model.ScriptDialect
import com.meadow.feature.script.domain.render.ScriptSyntaxColors
import com.meadow.feature.script.domain.render.ScriptSyntaxVisualTransformation
import com.meadow.feature.script.ui.state.ScriptEditorMode
import com.meadow.feature.script.ui.viewmodel.ScriptEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptEditorScreen(
    navController: NavController,
    viewModel: ScriptEditorViewModel
) {

    val snackbarHostState = remember { SnackbarHostState() }

    CollectUiMessages(
        messages = viewModel.uiMessages,
        snackbarHostState = snackbarHostState
    )

    val state = viewModel.uiState.collectAsState().value

    val colors = ScriptSyntaxColors(
        scene = MaterialTheme.colorScheme.primary,
        character = MaterialTheme.colorScheme.tertiary,
        dialogue = MaterialTheme.colorScheme.onSurface,
        section = MaterialTheme.colorScheme.secondary,
        metadata = MaterialTheme.colorScheme.secondary,
        keyword = MaterialTheme.colorScheme.primary,
        comment = MaterialTheme.colorScheme.onSurfaceVariant
    )

    val visualTransformation = remember(state.index.tokens, colors) {
        ScriptSyntaxVisualTransformation(
            tokens = state.index.tokens,
            colors = colors
        )
    }

    val outlineSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (state.mode == ScriptEditorMode.OUTLINE) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onModeChanged(ScriptEditorMode.STRUCTURE) },
            sheetState = outlineSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.script_outline),
                    style = MaterialTheme.typography.titleLarge
                )

                val outlineItems: List<com.meadow.feature.script.domain.parser.OutlineItem> =
                    state.index.outline
                for (item in outlineItems) {                    TextButton(
                    onClick = {
                        val tf = state.textFieldValue
                        val newSel = TextRange(item.position)
                        viewModel.onTextChanged(tf.copy(selection = newSel))
                        viewModel.onModeChanged(ScriptEditorMode.STRUCTURE)
                    },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(item.title)
                    }
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.script_editor_title)) },
                actions = {
                    ScriptTopToolbar(
                        dialect = state.dialect,
                        mode = state.mode,
                        onDialectChanged = viewModel::onDialectChanged,
                        onModeChanged = viewModel::onModeChanged,
                        onAiClick = { },
                        onExportClick = { },
                        onMoreClick = { }
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = state.textFieldValue,
                onValueChange = viewModel::onTextChanged,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                visualTransformation = visualTransformation
            )
        }
    }
}

@Composable
private fun ScriptTopToolbar(
    dialect: ScriptDialect,
    mode: ScriptEditorMode,
    onDialectChanged: (ScriptDialect) -> Unit,
    onModeChanged: (ScriptEditorMode) -> Unit,
    onAiClick: () -> Unit,
    onExportClick: () -> Unit,
    onMoreClick: () -> Unit
) {

    Row(
        modifier = Modifier.padding(end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        DialectDropdown(
            dialect = dialect,
            onDialectChanged = onDialectChanged
        )

        TextButton(
            onClick = { onModeChanged(ScriptEditorMode.STRUCTURE) },
            enabled = mode != ScriptEditorMode.STRUCTURE
        ) {
            Text(stringResource(R.string.script_mode_structure))
        }

        TextButton(
            onClick = { onModeChanged(ScriptEditorMode.OUTLINE) },
            enabled = mode != ScriptEditorMode.OUTLINE
        ) {
            Text(stringResource(R.string.script_mode_outline))
        }

        TextButton(
            onClick = { onModeChanged(ScriptEditorMode.CATALOG_LINKS) },
            enabled = mode != ScriptEditorMode.CATALOG_LINKS
        ) {
            Text(stringResource(R.string.script_mode_catalog_links))
        }

        TextButton(
            onClick = { onModeChanged(ScriptEditorMode.COMMENTS) },
            enabled = mode != ScriptEditorMode.COMMENTS
        ) {
            Text(stringResource(R.string.script_mode_comments))
        }

        IconButton(onClick = onAiClick) {
            Icon(Icons.Filled.Psychology, contentDescription = stringResource(R.string.script_action_ai))
        }

        IconButton(onClick = onExportClick) {
            Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.script_action_export))
        }

        IconButton(onClick = onMoreClick) {
            Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.script_action_more))
        }
    }
}

@Composable
private fun DialectDropdown(
    dialect: ScriptDialect,
    onDialectChanged: (ScriptDialect) -> Unit
) {

    val expanded = remember { mutableStateOf(false) }

    TextButton(onClick = { expanded.value = true }) {
        Text(dialectLabel(dialect))
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.script_dialect_fountain)) },
            onClick = {
                expanded.value = false
                onDialectChanged(ScriptDialect.FOUNTAIN)
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.script_dialect_renpy)) },
            onClick = {
                expanded.value = false
                onDialectChanged(ScriptDialect.RENPY)
            }
        )
    }
}

@Composable
private fun dialectLabel(dialect: ScriptDialect): String {
    return when (dialect) {
        ScriptDialect.FOUNTAIN -> stringResource(R.string.script_dialect_fountain)
        ScriptDialect.RENPY -> stringResource(R.string.script_dialect_renpy)
    }
}