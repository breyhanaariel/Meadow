package com.meadow.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R
import com.meadow.core.ui.components.tokens.ComponentRoles

enum class MeadowDialogType { Info, Alert, Confirm, Input }

@Composable
fun MeadowDialog(
    type: MeadowDialogType = MeadowDialogType.Info,
    title: String,
    message: String? = null,
    inputValue: String = "",
    onInputChange: ((String) -> Unit)? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val containerColor = when (type) {

        MeadowDialogType.Alert ->
            MaterialTheme.colorScheme.errorContainer

        MeadowDialogType.Confirm ->
            MaterialTheme.colorScheme.primaryContainer

        MeadowDialogType.Input,
        MeadowDialogType.Info ->
            MaterialTheme.colorScheme.surface
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                message?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (type == MeadowDialogType.Input) {
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { onInputChange?.invoke(it) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            MeadowButton(
                text = stringResource(R.string.action_confirm),
                onClick = onConfirm
            )
        },
        dismissButton = {
            MeadowButton(
                text = stringResource(R.string.action_cancel),
                type = MeadowButtonType.Ghost,
                onClick = onDismiss
            )
        },
        shape = ComponentRoles.Shape.RoundedMedium,
        containerColor = containerColor
    )
}

@Composable
fun MeadowInputDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    confirmEnabled: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    label = { Text(label) },
                    isError = error != null,
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            MeadowButton(
                text = stringResource(R.string.action_confirm),
                onClick = onConfirm,
                enabled = confirmEnabled
            )
        },
        dismissButton = {
            MeadowButton(
                text = stringResource(R.string.action_cancel),
                type = MeadowButtonType.Ghost,
                onClick = onDismiss
            )
        },
        shape = ComponentRoles.Shape.RoundedMedium,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun MeadowDialogScaffold(
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit = {},
    dismissButton: @Composable () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        shape = ComponentRoles.Shape.RoundedMedium,
        containerColor = MaterialTheme.colorScheme.surface
    )
}
