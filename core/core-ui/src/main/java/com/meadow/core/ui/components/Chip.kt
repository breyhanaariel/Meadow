package com.meadow.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.components.tokens.ComponentRoles

enum class MeadowChipType { Filter, Action, Status, Tag }

@Composable
fun MeadowChip(
    text: String,
    type: MeadowChipType = MeadowChipType.Tag,
    selected: Boolean = false,
    shapeOverride: Shape? = null,
    showBorder: Boolean = false,
    onToggle: (Boolean) -> Unit
) {
    val backgroundColor = when (type) {

        MeadowChipType.Filter ->
            if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface

        MeadowChipType.Action ->
            if (selected)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)

        MeadowChipType.Status ->
            if (selected)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f)

        MeadowChipType.Tag ->
            if (selected)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
    }

    val contentColor = when (type) {
        MeadowChipType.Filter ->
            MaterialTheme.colorScheme.onPrimaryContainer

        MeadowChipType.Action ->
            MaterialTheme.colorScheme.onSecondaryContainer

        MeadowChipType.Status ->
            MaterialTheme.colorScheme.onTertiaryContainer

        MeadowChipType.Tag ->
            MaterialTheme.colorScheme.onSurface
    }

    val border =
        if (showBorder)
            BorderStroke(
                1.dp,
                if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        else null

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = shapeOverride ?: ComponentRoles.Shape.Capsule,
        border = border,
        modifier = Modifier.clickable { onToggle(!selected) }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
