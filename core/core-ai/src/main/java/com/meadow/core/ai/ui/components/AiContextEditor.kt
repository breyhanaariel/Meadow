package com.meadow.core.ai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.R
import com.meadow.core.ai.data.context.AiContextEntity
import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowBottomSheet
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.ui.components.MeadowCard
import com.meadow.core.ui.components.MeadowCardFooter
import com.meadow.core.ui.components.MeadowCardType
import com.meadow.core.ui.components.MeadowDialog
import com.meadow.core.ui.components.MeadowDialogType
import com.meadow.core.ui.components.MeadowSearchField
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

enum class ContextSort {
    UPDATED_DESC,
    TITLE_ASC,
    PINNED_FIRST
}

@HiltViewModel
class AiContextEditorViewModel @Inject constructor(
    private val repo: AiContextRepository
) : ViewModel() {

    var contexts by mutableStateOf<List<AiContextEntity>>(emptyList())
        private set

    private var currentPersona: AiPersona? = null
    private var currentScopeKey: String? = null

    fun load(persona: AiPersona?, scopeKey: String?) = viewModelScope.launch {
        currentPersona = persona
        currentScopeKey = scopeKey
        contexts = repo.listForUiFiltered(persona, scopeKey)
    }

    private fun reload() = viewModelScope.launch {
        contexts = repo.listForUiFiltered(currentPersona, currentScopeKey)
    }

    fun delete(entity: AiContextEntity) = viewModelScope.launch {
        repo.delete(entity.id)
        reload()
    }

    fun toggleEnabled(entity: AiContextEntity) = viewModelScope.launch {
        repo.upsert(
            id = entity.id,
            title = entity.title,
            text = entity.text,
            category = entity.category,
            scopeKey = entity.scopeKey,
            personaKeysCsv = entity.personaKeysCsv,
            enabled = !entity.enabled,
            pinned = entity.pinned
        )
        reload()
    }

    fun togglePinned(entity: AiContextEntity) = viewModelScope.launch {
        repo.upsert(
            id = entity.id,
            title = entity.title,
            text = entity.text,
            category = entity.category,
            scopeKey = entity.scopeKey,
            personaKeysCsv = entity.personaKeysCsv,
            enabled = entity.enabled,
            pinned = !entity.pinned
        )
        reload()
    }

    fun save(
        id: String?,
        title: String,
        text: String,
        scopeKey: String?,
        personaKeysCsv: String?,
        enabled: Boolean,
        pinned: Boolean
    ) = viewModelScope.launch {

        repo.upsert(
            id = id,
            title = title,
            text = text,
            category = "General",
            scopeKey = scopeKey,
            personaKeysCsv = personaKeysCsv,
            enabled = enabled,
            pinned = pinned
        )

        reload()
    }
}

@Composable
fun AiContextEditor(
    scopeKey: String?,
    initialPersonaFilter: AiPersona? = null,
    onDismiss: () -> Unit,
    viewModel: AiContextEditorViewModel = hiltViewModel()
) {

    var personaFilter by remember { mutableStateOf(initialPersonaFilter) }
    var searchQuery by remember { mutableStateOf("") }
    var sortType by remember { mutableStateOf(ContextSort.UPDATED_DESC) }
    var showCreateSheet by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<AiContextEntity?>(null) }
    var editingEntity by remember { mutableStateOf<AiContextEntity?>(null) }

    LaunchedEffect(personaFilter, scopeKey) {
        viewModel.load(personaFilter, scopeKey)
    }

    MeadowBottomSheet(
        title = stringResource(R.string.ai_context_library),
        onDismiss = onDismiss
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            MeadowSearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = stringResource(R.string.ai_search_context)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortDropdown(
                        selected = sortType,
                        onSelect = { sortType = it }
                    )

                    PersonaDropdown(
                        selected = personaFilter,
                        onSelect = { personaFilter = it }
                    )
                }

                MeadowButton(
                    text = stringResource(CoreUiR.string.action_add),
                    onClick = {
                        editingEntity = null
                        showCreateSheet = true
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            val filtered = remember(viewModel.contexts, searchQuery, sortType, personaFilter) {
                viewModel.contexts
                    .filter { entity ->
                        val matchesSearch =
                            entity.title.contains(searchQuery, true) ||
                                    entity.text.contains(searchQuery, true)

                        val matchesPersona =
                            personaFilter == null ||
                                    entity.personaKeysCsv
                                        ?.split(",")
                                        ?.map { it.trim() }
                                        ?.any { it.equals(personaFilter!!.name, true) } == true

                        matchesSearch && matchesPersona
                    }
                    .let { list ->
                        when (sortType) {
                            ContextSort.UPDATED_DESC -> list.sortedByDescending { it.updatedAtUtcMs }
                            ContextSort.TITLE_ASC -> list.sortedBy { it.title.lowercase() }
                            ContextSort.PINNED_FIRST -> list.sortedWith(
                                compareByDescending<AiContextEntity> { it.pinned }
                                    .thenByDescending { it.updatedAtUtcMs }
                            )
                        }
                    }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered, key = { it.id }) { item ->
                    AiContextRow(
                        entity = item,
                        onToggleEnabled = { viewModel.toggleEnabled(item) },
                        onTogglePinned = { viewModel.togglePinned(item) },
                        onEdit = {
                            editingEntity = item
                            showCreateSheet = true
                        },
                        onDelete = { pendingDelete = item }
                    )
                }
            }
        }
    }

    if (showCreateSheet) {
        ContextCreateEditSheet(
            entity = editingEntity,
            scopeKey = scopeKey,
            onDismiss = { showCreateSheet = false },
            onSave = { id, title, text, personaCsv, enabled, pinned ->
                viewModel.save(
                    id = id,
                    title = title,
                    text = text,
                    scopeKey = scopeKey,
                    personaKeysCsv = personaCsv,
                    enabled = enabled,
                    pinned = pinned
                )
                showCreateSheet = false
            }
        )
    }

    if (pendingDelete != null) {
        MeadowDialog(
            type = MeadowDialogType.Alert,
            title = stringResource(R.string.ai_delete_context_title),
            message = stringResource(
                R.string.ai_delete_context_message,
                pendingDelete!!.title
            ),
            onConfirm = {
                viewModel.delete(pendingDelete!!)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
private fun AiContextRow(
    entity: AiContextEntity,
    onToggleEnabled: () -> Unit,
    onTogglePinned: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    MeadowCard(
        modifier = Modifier.fillMaxWidth(),
        type = MeadowCardType.Content
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )

                    val personaLabel = entity.personaKeysCsv
                        ?.split(",")
                        ?.map { it.trim() }
                        ?.filter { it.isNotBlank() }
                        ?.joinToString(", ")
                        ?: stringResource(R.string.ai_context_all_personas)

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = personaLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = entity.enabled,
                    onCheckedChange = { onToggleEnabled() }
                )

                IconButton(onClick = onTogglePinned) {
                    Icon(
                        imageVector = if (entity.pinned) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            MeadowCardFooter {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun PersonaDropdown(
    selected: AiPersona?,
    onSelect: (AiPersona?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        AssistChip(
            onClick = { expanded = true },
            label = {
                Text(
                    selected?.displayName
                        ?: stringResource(R.string.ai_context_all_personas)
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            DropdownMenuItem(
                text = { Text(stringResource(R.string.ai_context_all_personas)) },
                onClick = {
                    expanded = false
                    onSelect(null)
                }
            )

            AiPersona.values().forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona.displayName) },
                    onClick = {
                        expanded = false
                        onSelect(persona)
                    }
                )
            }
        }
    }
}

@Composable
private fun SortDropdown(
    selected: ContextSort,
    onSelect: (ContextSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(stringResource(CoreUiR.string.action_sort)) }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            ContextSort.values().forEach { sort ->
                val label = when (sort) {
                    ContextSort.UPDATED_DESC -> stringResource(CoreUiR.string.sort_updated)
                    ContextSort.TITLE_ASC -> stringResource(CoreUiR.string.sort_title)
                    ContextSort.PINNED_FIRST -> stringResource(CoreUiR.string.sort_pinned)
                }

                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        onSelect(sort)
                    }
                )
            }
        }
    }
}

@Composable
private fun ContextCreateEditSheet(
    entity: AiContextEntity?,
    scopeKey: String?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        text: String,
        personaCsv: String?,
        enabled: Boolean,
        pinned: Boolean
    ) -> Unit
) {

    var title by remember { mutableStateOf(entity?.title ?: "") }
    var body by remember { mutableStateOf(entity?.text ?: "") }
    var enabled by remember { mutableStateOf(entity?.enabled ?: true) }
    var pinned by remember { mutableStateOf(entity?.pinned ?: false) }

    var selectedPersonas by remember {
        mutableStateOf(
            entity?.personaKeysCsv
                ?.split(",")
                ?.mapNotNull { key ->
                    val trimmed = key.trim()
                    if (trimmed.isBlank()) null else runCatching { AiPersona.valueOf(trimmed) }.getOrNull()
                }
                ?.toSet()
                ?: emptySet()
        )
    }

    MeadowBottomSheet(
        title = if (entity == null)
            stringResource(R.string.ai_new_context)
        else
            stringResource(R.string.ai_edit_context),
        onDismiss = onDismiss
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.ai_context_title)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text(stringResource(R.string.ai_context_body)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6
            )

            MultiPersonaSelector(
                selected = selectedPersonas,
                onChange = { selectedPersonas = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(CoreUiR.string.action_enabled))
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it }
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(CoreUiR.string.sort_pinned))
                    Switch(
                        checked = pinned,
                        onCheckedChange = { pinned = it }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                ) {
                    Text(stringResource(CoreUiR.string.action_cancel))
                }

                MeadowButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(CoreUiR.string.action_save),
                    onClick = {
                        val personaCsv = selectedPersonas
                            .map { it.name }
                            .sorted()
                            .joinToString(",")
                            .ifBlank { null }

                        onSave(
                            entity?.id,
                            title,
                            body,
                            personaCsv,
                            enabled,
                            pinned
                        )
                    }
                )
            }

        }
    }
}

@Composable
private fun MultiPersonaSelector(
    selected: Set<AiPersona>,
    onChange: (Set<AiPersona>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val label = remember(selected) {
        if (selected.isEmpty()) null
        else selected.map { it.displayName }.sorted().joinToString(", ")
    }

    Box {
        AssistChip(
            onClick = { expanded = true },
            label = {
                Text(
                    label ?: stringResource(R.string.ai_context_all_personas)
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            DropdownMenuItem(
                text = { Text(stringResource(R.string.ai_context_all_personas)) },
                onClick = {
                    onChange(emptySet())
                    expanded = false
                }
            )

            AiPersona.values().forEach { persona ->
                val isChecked = persona in selected

                DropdownMenuItem(
                    text = { Text(persona.displayName) },
                    onClick = {
                        onChange(
                            if (isChecked) selected - persona else selected + persona
                        )
                    },
                    trailingIcon = {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null
                        )
                    }
                )
            }
        }
    }
}
