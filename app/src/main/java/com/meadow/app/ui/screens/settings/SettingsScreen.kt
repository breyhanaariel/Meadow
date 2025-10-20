package com.meadow.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meadow.app.ui.components.PastelButton
import com.meadow.app.ui.theme.*
import com.meadow.app.utils.EncryptionUtils
import com.meadow.app.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

/**
 * SettingsScreen.kt
 *
 * The enhanced Meadow Control Center ✨
 * Future-proof, secure, multi-user, and project-aware configuration screen.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val theme by settingsViewModel.currentTheme.collectAsState(initial = "Lavender Dream")
    val googleSettings by settingsViewModel.googleSettings.collectAsState(initial = emptyMap())
    val geminiPrompts by settingsViewModel.geminiPrompts.collectAsState(initial = emptyMap())
    val currentProject by settingsViewModel.currentProject.collectAsState(initial = "Global")
    val collaborators by settingsViewModel.collaborators.collectAsState(initial = emptyList())

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings ⚙️", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MeadowGradients.LavenderPink)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // 🎨 Theme Selection
            CollapsibleSection(title = "🎨 Appearance") {
                Text("Current Theme: $theme", color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                PastelButton(
                    text = "Change Theme 🌈",
                    onClick = { settingsViewModel.cycleTheme() }
                )
            }

            // ☁️ Sync & Backup
            CollapsibleSection(title = "☁️ Sync & Backup") {
                var firebaseSync by remember { mutableStateOf(true) }
                var driveBackup by remember { mutableStateOf(true) }

                SwitchRow("Enable Firebase Sync", firebaseSync) { firebaseSync = it }
                SwitchRow("Enable Google Drive Backup", driveBackup) { driveBackup = it }

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PastelButton(
                        text = "💾 Export Settings",
                        onClick = {
                            settingsViewModel.exportSettingsJson(context)
                        },
                        icon = Icons.Default.Upload
                    )
                    PastelButton(
                        text = "📥 Import Settings",
                        onClick = {
                            settingsViewModel.importSettingsJson(context)
                        },
                        icon = Icons.Default.Download
                    )
                }
            }

            // 🔑 Google API Configuration (Encrypted)
            CollapsibleSection(title = "🔑 Google API Keys (Encrypted)") {
                googleSettings.forEach { (service, key) ->
                    EditableEncryptedApiField(
                        label = service.replaceFirstChar { it.uppercase() },
                        value = key,
                        onSave = { newValue ->
                            val encrypted = EncryptionUtils.encrypt(newValue)
                            settingsViewModel.updateGoogleApiKey(service, encrypted)
                        },
                        onTest = {
                            coroutineScope.launch {
                                val success = settingsViewModel.testGoogleServiceConnection(service)
                                Toast.makeText(
                                    context,
                                    if (success) "$service connected!" else "Connection failed ❌",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }

            // 🌸 Gemini Personality Prompts (Project-Linked)
            CollapsibleSection(title = "🧠 Gemini Prompts (Project: $currentProject)") {
                EditablePromptField("🌱 Sprout - Idea Generator", geminiPrompts["sprout"] ?: "") {
                    settingsViewModel.updateGeminiPrompt("sprout", it)
                }
                EditablePromptField("🌸 Bloom - Critique & Editor", geminiPrompts["bloom"] ?: "") {
                    settingsViewModel.updateGeminiPrompt("bloom", it)
                }
                EditablePromptField("🦄 Meadow - Story Helper", geminiPrompts["meadow"] ?: "") {
                    settingsViewModel.updateGeminiPrompt("meadow", it)
                }

                Spacer(modifier = Modifier.height(8.dp))
                PastelButton(
                    text = "Link Prompts to Current Project 🔗",
                    onClick = { settingsViewModel.linkPromptsToProject(currentProject) }
                )
            }

            // 👥 Multi-user Collaboration Settings
            CollapsibleSection(title = "👥 Collaborators & Access") {
                collaborators.forEach { user ->
                    Text("• ${user.name} (${user.email})", color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                PastelButton(
                    text = "Invite Collaborator ✉️",
                    icon = Icons.Default.PersonAdd,
                    onClick = {
                        settingsViewModel.inviteCollaborator()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🌼 Save Button
            PastelButton(
                text = "Save All Settings ✨",
                onClick = { settingsViewModel.saveAllSettings() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 🌸 Collapsible Section (unchanged)
 */
@Composable
fun CollapsibleSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(PastelPeach, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextPrimary)
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

/**
 * 🧁 Reusable Switch Row
 */
@Composable
fun SwitchRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextPrimary)
        Switch(
            checked = value,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PastelMint,
                uncheckedThumbColor = PastelBlue
            )
        )
    }
}

/**
 * 🔒 Editable & Encrypted API Field with "Test Connection"
 */
@Composable
fun EditableEncryptedApiField(
    label: String,
    value: String,
    onSave: (String) -> Unit,
    onTest: () -> Unit
) {
    var text by remember { mutableStateOf(EncryptionUtils.decrypt(value)) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Enter API Key") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PastelButton(text = "Save", onClick = { onSave(text) })
            PastelButton(text = "Test Connection", onClick = onTest)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * 🌸 Editable Gemini Prompt Field
 */
@Composable
fun EditablePromptField(label: String, value: String, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(value) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Edit AI Prompt") },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PastelButton(text = "Save Prompt", onClick = { onSave(text) })
        Spacer(modifier = Modifier.height(8.dp))
    }
}