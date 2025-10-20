package com.meadow.app.ui.components.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.meadow.app.domain.model.MindMapNode

@Composable
fun MindMapNodeDialog(
    node: MindMapNode? = null,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var title by remember { mutableStateOf(node?.title ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (node == null) "Add Node" else "Edit Node") },
        text = { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }) },
        confirmButton = { TextButton(onClick = { onSave(title) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}