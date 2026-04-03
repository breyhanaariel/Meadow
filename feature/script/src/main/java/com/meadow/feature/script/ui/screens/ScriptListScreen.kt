package com.meadow.feature.script.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meadow.core.ui.events.CollectUiMessages
import com.meadow.feature.script.R
import com.meadow.feature.script.domain.model.ScriptDialect
import com.meadow.feature.script.domain.model.ScriptType
import com.meadow.feature.script.ui.viewmodel.ScriptListViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptListScreen(
    navController: NavController,
    viewModel: ScriptListViewModel,
    projectId: String
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(projectId) {
        viewModel.bind(projectId)
    }

    LaunchedEffect(Unit) {
        viewModel.navRoutes.collectLatest { route: String ->
            navController.navigate(route)
        }
    }

    CollectUiMessages(
        messages = viewModel.uiMessages,
        snackbarHostState = snackbarHostState
    )

    val state = viewModel.uiState.collectAsState().value

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.script_list_title)) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createDefaultScript(projectId) }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) { padding ->

        if (!state.isLoading && state.items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.script_no_scripts),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = state.items,
                key = { item -> item.scriptId }
            ) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openScript(item.scriptId) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(scriptTypeLabel(item.type)),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(scriptDialectLabel(item.dialect)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun scriptTypeLabel(type: ScriptType): Int {
    return when (type) {
        ScriptType.COMIC -> R.string.script_type_comic
        ScriptType.NOVEL -> R.string.script_type_novel
        ScriptType.TV -> R.string.script_type_tv
        ScriptType.MOVIE -> R.string.script_type_movie
        ScriptType.GAME -> R.string.script_type_game
    }
}

private fun scriptDialectLabel(dialect: ScriptDialect): Int {
    return when (dialect) {
        ScriptDialect.FOUNTAIN -> R.string.script_dialect_fountain
        ScriptDialect.RENPY -> R.string.script_dialect_renpy
    }
}