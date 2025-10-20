package com.meadow.app.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meadow.app.domain.model.PlotCard

@Composable
fun PlotCardDialog(
    card: PlotCard? = null,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(card?.title ?: "") }
    var summary by remember { mutableStateOf(card?.summary ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (card == null) "Add Plot Card" else "Edit Plot Card") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary") },
                    modifier = Modifier.height(120.dp)
                )
            }
        },
        confirmButton = { TextButton(onClick = { onSave(title, summary) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}