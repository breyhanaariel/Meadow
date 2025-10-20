package com.meadow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.meadow.app.ui.components.InlineFormattingToolbar
import com.meadow.app.ui.components.PastelButton
import com.meadow.app.ui.theme.*
import com.meadow.app.viewmodel.CatalogViewModel
import com.meadow.app.viewmodel.ScriptViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ScriptEditorScreen.kt
 *
 * Allows selecting and editing scripts for a project.
 * Includes tabs for switching between the Script Editor and Catalog view.
 * Features inline formatting toolbar and auto-save.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptEditorScreen(
    projectId: String,
    scriptViewModel: ScriptViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    catalogViewModel: CatalogViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val scripts by scriptViewModel.getScriptsForProject(projectId).collectAsState(initial = emptyList())
    var selectedScript by remember { mutableStateOf(scripts.firstOrNull()) }
    var scriptContent by remember { mutableStateOf(selectedScript?.content ?: "") }

    var activeTab by remember { mutableStateOf("Script") }

    // Auto-save coroutine every 10 seconds
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(scriptContent, selectedScript) {
        coroutineScope.launch {
            delay(10_000)
            selectedScript?.let {
                scriptViewModel.updateScript(it.copy(content = scriptContent))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Script Editor ✨", color = TextPrimary) },
                actions = {
                    IconButton(onClick = {
                        selectedScript?.let {
                            scriptViewModel.updateScript(it.copy(content = scriptContent))
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Script", tint = TextPrimary)
                    }
                },
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
        ) {
            // 🪄 Script Dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                var expanded by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = PastelPeach)
                ) {
                    Text(selectedScript?.title ?: "Select Script", color = TextPrimary)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    scripts.forEach { script ->
                        DropdownMenuItem(
                            text = { Text(script.title, color = TextPrimary) },
                            onClick = {
                                selectedScript = script
                                scriptContent = script.content
                                expanded = false
                            }
                        )
                    }

                    Divider()

                    DropdownMenuItem(
                        text = { Text("➕ Create New Script", color = TextPrimary) },
                        onClick = {
                            val newScript = scriptViewModel.createScript(projectId, "New Script")
                            selectedScript = newScript
                            scriptContent = ""
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🧭 Tabs for Script / Catalog
            TabRow(
                selectedTabIndex = if (activeTab == "Script") 0 else 1,
                containerColor = PastelMint
            ) {
                Tab(
                    selected = activeTab == "Script",
                    onClick = { activeTab = "Script" },
                    text = { Text("Script ✍️", color = TextPrimary) }
                )
                Tab(
                    selected = activeTab == "Catalog",
                    onClick = { activeTab = "Catalog" },
                    text = { Text("Catalog 📖", color = TextPrimary) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (activeTab) {
                "Script" -> ScriptEditorContent(
                    scriptContent = scriptContent,
                    onContentChange = { scriptContent = it }
                )
                "Catalog" -> CatalogQuickView(catalogViewModel)
            }
        }
    }
}

/**
 * ✨ Script Editor Area
 */
@Composable
fun ScriptEditorContent(
    scriptContent: String,
    onContentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(PastelPeach, RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        InlineFormattingToolbar(onFormat = { tag ->
            // Apply markdown/fountain formatting tags
        })

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = scriptContent,
            onValueChange = onContentChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = TextPrimary,
                lineHeight = 22.sp
            ),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        )
    }
}

/**
 * 📖 Quick Catalog Access Panel
 */
@Composable
fun CatalogQuickView(catalogViewModel: CatalogViewModel) {
    val catalogItems by catalogViewModel.allCatalogItems.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(PastelBlue, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text("Linked Catalog Items", color = TextPrimary, style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        catalogItems.take(10).forEach {
            Text("• ${it.name} (${it.type})", color = TextSecondary, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        PastelButton(
            text = "Open Full Catalog",
            onClick = { /* Navigate to Catalog Screen */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
}