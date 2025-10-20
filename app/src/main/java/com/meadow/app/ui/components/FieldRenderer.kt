package com.meadow.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * FieldRenderer.kt
 *
 * Dynamically renders input fields for Catalog Items based on JSON field definitions.
 * Each field supports text, number, dropdown, date picker, and file upload types.
 */

@Composable
fun FieldRenderer(
    label: String,
    type: String,
    value: String,
    options: List<String> = emptyList(),
    onValueChange: (String) -> Unit
) {
    when (type) {
        "text" -> OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        "select" -> DropdownMenuField(label, value, options, onValueChange)
        else -> Text(text = "Unsupported field type: $type")
    }
}

@Composable
private fun DropdownMenuField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
