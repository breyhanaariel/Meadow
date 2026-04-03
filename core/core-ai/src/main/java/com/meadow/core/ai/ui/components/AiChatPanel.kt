package com.meadow.core.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.meadow.core.ai.R
import com.meadow.core.ai.domain.model.*
import com.meadow.core.ai.viewmodel.*
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.ui.components.MeadowButtonType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatPanel(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AiChatViewModel,
    onDismiss: () -> Unit,
    onClose: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        println("AI VM instance: ${viewModel.hashCode()}")
    }

    val state by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    /* ─── COLLECT UI EVENTS ───────────────────── */



/* ─── DROPDOWN STATE ───────────────────── */
    /* Controls chat selection dropdown visibility */
    var expanded by remember { mutableStateOf(false) }
    var showPersonaSettings by remember { mutableStateOf(false) }

    /* ─── AUTO SCROLL ───────────────────── */
    /* Scrolls to latest message when messages update */
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    /* ─── ROOT LAYOUT ───────────────────── */
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 16.dp)
    ) {

        /* ─── PERSONA SELECTOR + CLOSE ───────────────────── */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = Modifier.weight(1f)) {
                AiPersonaSelector(
                    selected = state.persona,
                    onSelect = { viewModel.onEvent(AiChatEvent.PersonaChanged(it)) }
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        /* ─── CONTEXT LABEL ───────────────────── */
        /* Displays optional contextual scope for the chat */
        if (state.contextLabel != null) {
            Text(
                text = "Context: ${state.contextLabel}",
                style = MaterialTheme.typography.labelSmall
            )
        }

        /* ─── CHAT SELECTION CONTROLS ───────────────────── */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
            ) {
                MeadowButton(
                    text = state.selectedChatName
                        ?: stringResource(R.string.ai_choose_chat),
                    type = MeadowButtonType.Secondary,
                    onClick = { expanded = true }
                )
            }

            if (state.selectedChatId != null) {
                Box(modifier = Modifier.clip(RoundedCornerShape(50))) {
                    MeadowButton(
                        text = stringResource(R.string.ai_rename_chat),
                        type = MeadowButtonType.Secondary,
                        onClick = {
                            viewModel.onEvent(AiChatEvent.RenameChatClicked)
                        }
                    )
                }
            }
        }

        /* ─── CHAT DROPDOWN MENU ───────────────────── */
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.ai_new_chat),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                onClick = {
                    expanded = false
                    viewModel.onEvent(AiChatEvent.NewChatClicked(state.persona))
                }
            )

            Divider()

            state.chatSummaries.forEach { summary ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = summary.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        expanded = false
                        viewModel.onEvent(AiChatEvent.ChatSelected(summary.id))
                    }
                )
            }

            if (state.chatSummaries.isEmpty()) {
                DropdownMenuItem(
                    enabled = false,
                    text = {
                        Text(
                            text = stringResource(R.string.ai_no_chats),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {}
                )
            }
        }

        /* ─── PDF CONTEXT SELECTOR ───────────────────── */
        /* Only available for Meadow persona */
        if (state.persona == AiPersona.Meadow) {
            Spacer(Modifier.height(6.dp))

            MeadowPdfDropdown(
                pdfDocuments = state.pdfDocuments,
                selectedPdf = state.selectedPdf,
                onSelect = {
                    viewModel.onEvent(AiChatEvent.PdfDocumentSelected(it))
                    viewModel.onEvent(AiChatEvent.OpenPdfViewer(it))
                }
            )

            Spacer(Modifier.height(8.dp))
        }

        /* ─── EMPTY CHAT STATE ───────────────────── */
        if (state.selectedChatId == null) {
            PersonaIntroCard(
                persona = state.persona,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onNewChatClicked = {
                    viewModel.onEvent(AiChatEvent.NewChatClicked(state.persona))
                },
                onOpenPersonaSettings = {
                    showPersonaSettings = true
                },
                onClose = onClose
            )

        } else {

            /* ─── CHAT HISTORY ───────────────────── */
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    AiResponseCard(
                        persona = state.persona,
                        message = msg.content,
                        isUser = msg.isUser,
                        onEvent = viewModel::onEvent
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        /* ─── MESSAGE INPUT ───────────────────── */
        OutlinedTextField(
            value = state.input,
            onValueChange = { viewModel.onEvent(AiChatEvent.InputChanged(it)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    text = state.persona.placeholderText(),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.selectedChatId != null,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(Modifier.height(12.dp))

        /* ─── SEND ACTION ───────────────────── */
        MeadowButton(
            text = if (state.isLoading)
                stringResource(R.string.ai_thinking)
            else
                stringResource(CoreUiR.string.action_send),
            type = MeadowButtonType.Primary,
            enabled = !state.isLoading &&
                    state.input.isNotBlank() &&
                    state.selectedChatId != null,
            onClick = { viewModel.onEvent(AiChatEvent.SendPressed) }
        )

        /* ─── NEW CHAT DIALOG ───────────────────── */
        if (state.showNewChatDialog) {

            var name by remember { mutableStateOf("") }
            val isValid = name.isNotBlank()

            AlertDialog(
                onDismissRequest = {
                    viewModel.onEvent(AiChatEvent.NewChatDialogDismissed)
                },
                title = {
                    Text(
                        text = stringResource(R.string.ai_new_chat),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            label = {
                                Text(
                                    text = stringResource(R.string.ai_chat_name),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        if (!isValid) {
                            Text(
                                text = stringResource(CoreUiR.string.error_name_required),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = isValid,
                        onClick = {
                            viewModel.onEvent(
                                AiChatEvent.NewChatConfirmed(name.trim())
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.ai_create_new_chat),
                            color = if (isValid)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.onEvent(AiChatEvent.NewChatDialogDismissed)
                        }
                    ) {
                        Text(
                            text = stringResource(CoreUiR.string.action_cancel),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
        }

        /* ─── RENAME CHAT DIALOG ───────────────────── */
        if (state.showRenameChatDialog) {

            var name by remember { mutableStateOf(state.selectedChatName ?: "") }
            val isValid = name.isNotBlank()

            AlertDialog(
                onDismissRequest = {
                    viewModel.onEvent(AiChatEvent.RenameChatDialogDismissed)
                },
                title = {
                    Text(
                        text = stringResource(R.string.ai_rename_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            label = {
                                Text(
                                    text = stringResource(R.string.ai_chat_name),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        if (!isValid) {
                            Text(
                                text = stringResource(CoreUiR.string.error_name_required),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = isValid,
                        onClick = {
                            viewModel.onEvent(
                                AiChatEvent.RenameChatConfirmed(name.trim())
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(CoreUiR.string.action_save),
                            color = if (isValid)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.onEvent(
                                AiChatEvent.RenameChatDialogDismissed
                            )
                        }
                    ) {
                        Text(
                            text = stringResource(CoreUiR.string.action_cancel),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
        }
        if (showPersonaSettings) {
            PersonaPromptBottomSheet(
                persona = state.persona,
                onDismiss = { showPersonaSettings = false }
            )
        }
    }
}



/* ─── PERSONA INTRO CARD ───────────────────── */
@Composable
fun PersonaIntroCard(
    persona: AiPersona,
    modifier: Modifier = Modifier,
    onNewChatClicked: () -> Unit,
    onOpenPersonaSettings: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(22.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(persona.iconRes),
                    contentDescription = persona.displayName,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp)
                )
            }

            Text(
                text = persona.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = persona.descriptionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            MeadowButton(
                text = stringResource(R.string.ai_settings_title),
                type = MeadowButtonType.Secondary,
                onClick = onOpenPersonaSettings
            )

            MeadowButton(
                text = stringResource(R.string.ai_create_new_chat),
                type = MeadowButtonType.Primary,
                onClick = onNewChatClicked
            )
        }
    }
}

/* ─── PDF DROPDOWN ───────────────────── */
@Composable
fun MeadowPdfDropdown(
    pdfDocuments: List<PdfDocument>,
    selectedPdf: PdfDocument?,
    onSelect: (PdfDocument) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    if (pdfDocuments.isEmpty()) return

    OutlinedButton(
        onClick = { expanded = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = selectedPdf?.title
                ?: stringResource(R.string.ai_select_writing_guide),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        pdfDocuments.forEach { pdf ->
            DropdownMenuItem(
                text = { Text(pdf.title) },
                onClick = {
                    expanded = false
                    onSelect(pdf)
                }
            )
        }
    }
}
