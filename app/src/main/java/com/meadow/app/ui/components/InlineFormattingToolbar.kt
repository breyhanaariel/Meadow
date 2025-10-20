package com.meadow.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * InlineFormattingToolbar.kt
 *
 * Toolbar for the script editor to apply formatting
 * (bold, italic, underline, etc.) with Fountain syntax support.
 */

@Composable
fun InlineFormattingToolbar(
    onAction: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ToolbarButton("B") { onAction("bold") }
        ToolbarButton("I") { onAction("italic") }
        ToolbarButton("U") { onAction("underline") }
        ToolbarButton("A") { onAction("align") }
        ToolbarButton("Img") { onAction("image") }
        ToolbarButton("🔄") { onAction("undo") }
        ToolbarButton("♻️") { onAction("redo") }
    }
}

@Composable
fun ToolbarButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 48.dp, height = 36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3D1F4))
    ) {
        Text(label)
    }
}
