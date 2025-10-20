package com.meadow.app.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meadow.app.domain.model.TimelineEvent

@Composable
fun TimelineEventDialog(
    event: TimelineEvent? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Add Timeline Event" else "Edit Timeline Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.height(100.dp)
                )
            }
        },
        confirmButton = { TextButton(onClick = { onSave(title, description, date) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}