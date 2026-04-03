package com.meadow.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.components.tokens.ComponentRoles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeadowBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    dragHandle: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = ComponentRoles.Shape.Sheet,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = ComponentRoles.Elevation.Medium,
        dragHandle = {
            if (dragHandle) {
                BottomSheetDefaults.DragHandle()
            }
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    ComponentRoles.Shape.Sheet
                )
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            content()
        }
    }
}
