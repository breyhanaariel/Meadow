package com.meadow.feature.catalog.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.events.CollectUiMessages
import com.meadow.core.ui.locale.LocalizedContext
import com.meadow.feature.catalog.ui.components.CatalogDeleteConfirmDialog
import com.meadow.feature.catalog.ui.navigation.CatalogRoutes
import com.meadow.feature.catalog.ui.viewmodel.CatalogItemUiState
import com.meadow.feature.catalog.ui.viewmodel.CatalogItemViewModel
import com.meadow.feature.catalog.R as R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogItemScreen(
    navController: NavController,
    itemId: String,
    viewModel: CatalogItemViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val localized = LocalizedContext.current

    LaunchedEffect(itemId) {
        viewModel.load(itemId)
    }

    val state: CatalogItemUiState by viewModel.uiState.collectAsState()

    var kebabExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    CollectUiMessages(messages = viewModel.uiMessages, snackbarHostState = snackbarHostState)

    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    val visibleFields = remember(state.fields) {
        state.fields.filter { field ->
            val raw = field.value?.rawValue
            when (raw) {
                null -> false
                is String -> raw.trim().isNotBlank()
                else -> raw.toString().trim().isNotBlank()
            }
        }
    }

    val grouped = remember(visibleFields) {
        val groups =
            visibleFields.groupBy { field ->
                val section = field.definition.metadata["section"] as? String
                section?.trim().takeUnless { it.isNullOrBlank() } ?: "General"
            }
        groups
            .mapValues { (_, list) -> list.sortedBy { it.definition.order } }
            .toSortedMap(compareBy { it.lowercase() })
    }

    if (showDeleteConfirm && state.itemId != null) {
        CatalogDeleteConfirmDialog(
            catalogTitle = state.title ?: state.itemId!!,
            onConfirmDelete = {
                showDeleteConfirm = false
                viewModel.delete()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect {
            navController.popBackStack()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(state.title ?: localized.getString(R.string.catalog_item))
                },
                actions = {
                    IconButton(onClick = { kebabExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = kebabExpanded,
                        onDismissRequest = { kebabExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(localized.getString(CoreUiR.string.action_edit)) },
                            onClick = {
                                kebabExpanded = false
                                state.itemId?.let { id ->
                                    navController.navigate(CatalogRoutes.catalogEditItem(id))
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(localized.getString(CoreUiR.string.action_delete)) },
                            onClick = {
                                kebabExpanded = false
                                showDeleteConfirm = true
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.itemId == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(localized.getString(CoreUiR.string.load_failed))
                }
            }

            visibleFields.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(localized.getString(CoreUiR.string.no_content))
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    grouped.forEach { (section, fields) ->
                        val expanded = expandedMap[section] ?: true

                        SectionCard(
                            title = section,
                            expanded = expanded,
                            onToggle = {
                                expandedMap[section] = !(expandedMap[section] ?: true)
                            }
                        ) {
                            fields.forEachIndexed { index, field ->
                                ReadOnlyFieldRow(
                                    label = resolveStringKey(localized, field.definition.labelKey),
                                    value = formatRawValue(field.value?.rawValue)
                                )

                                if (index != fields.lastIndex) {
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyFieldRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Localization-safe resolver */
private fun resolveStringKey(
    localized: android.content.Context,
    key: String
): String =
    runCatching {
        val resId = localized.resources.getIdentifier(key, "string", localized.packageName)
        if (resId != 0) localized.getString(resId) else key
    }.getOrElse { key }

private fun formatRawValue(raw: Any?): String =
    when (raw) {
        null -> ""
        is String -> raw.trim()
        is Iterable<*> -> raw.joinToString(", ") { it?.toString().orEmpty() }.trim()
        is Array<*> -> raw.joinToString(", ") { it?.toString().orEmpty() }.trim()
        else -> raw.toString().trim()
    }
