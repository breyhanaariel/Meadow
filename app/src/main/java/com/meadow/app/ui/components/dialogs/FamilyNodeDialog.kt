package com.meadow.app.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meadow.app.domain.model.FamilyNode

@Composable
fun FamilyNodeDialog(
    node: FamilyNode? = null,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(node?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (node == null) "Add Family Member" else "Edit Family Member") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        },
        confirmButton = { TextButton(onClick = { onSave(name) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}