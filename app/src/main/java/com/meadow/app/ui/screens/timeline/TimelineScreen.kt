package com.meadow.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meadow.app.domain.model.TimelineEvent
import com.meadow.app.viewmodel.TimelineViewModel

/**
 * TimelineScreen.kt
 *
 * Interactive story timeline for worldbuilding and events.
 * - Add/edit/delete events
 * - Recurring events support
 * - Sparkles when saving/syncing
 */

@Composable
fun TimelineScreen(viewModel: TimelineViewModel) {
    val events by viewModel.timelineEvents.collectAsState()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Timeline", style = MaterialTheme.typography.titleLarge)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(events) { event ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(8.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleMedium)
                        Text(event.date.toString(), style = MaterialTheme.typography.bodySmall)
                        Text(event.description, style = MaterialTheme.typography.bodySmall)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            PastelButton("Edit") { viewModel.editEvent(event) }
                            Spacer(Modifier.width(8.dp))
                            PastelButton("Delete") { viewModel.deleteEvent(event.id) }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.addEvent() },
            modifier = Modifier.align(Alignment.End)
        ) { Text("+") }
    }
}