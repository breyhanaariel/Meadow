package com.meadow.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.components.tokens.ComponentRoles

@Composable
fun MeadowModal(
    visible: Boolean,
    scrimColor: Color = Color.Black.copy(alpha = 0.4f),
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (!visible) return

    Dialog(
        onDismissRequest = { onDismissRequest?.invoke() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = ComponentRoles.Shape.BubbleSoft,
                tonalElevation = ComponentRoles.Elevation.High,
                modifier = Modifier.padding(24.dp)
            ) {
                content()
            }
        }
    }
}
