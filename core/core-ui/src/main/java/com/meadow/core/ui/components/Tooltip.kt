package com.meadow.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.components.tokens.ComponentRoles

@Composable
fun MeadowTooltip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = ComponentRoles.Shape.BubbleSoft,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = ComponentRoles.Elevation.Low,
        modifier = modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(10.dp)
        )
    }
}
