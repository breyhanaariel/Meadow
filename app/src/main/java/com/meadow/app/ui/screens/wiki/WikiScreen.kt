package com.meadow.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.meadow.app.ui.components.*
import com.meadow.app.viewmodel.WikiViewModel

/**
 * WikiScreen.kt
 *
 * Displays a searchable, editable wiki for the project.
 * - Uses Markdown/Fountain parsing
 * - Supports inline linking to catalog or scripts
 */

@Composable
fun WikiScreen(
    viewModel: WikiViewModel,
    onAddEntry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.wikiEntries.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PastelSearchBar(
            value = searchText.text,
            onValueChange = { searchText = TextFieldValue(it) },
            placeholder = "Search wiki entries..."
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries.filter { it.title.contains(searchText.text, true) }) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(entry.title, style = MaterialTheme.typography.titleMedium)
                        Text(entry.content.take(200) + "...", style = MaterialTheme.typography.bodySmall)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            PastelButton(text = "Edit") { viewModel.editEntry(entry.id) }
                            Spacer(Modifier.width(8.dp))
                            PastelButton(text = "Delete") { viewModel.deleteEntry(entry.id) }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddEntry,
            modifier = Modifier.align(Alignment.End)
        ) { Text("+") }
    }
}