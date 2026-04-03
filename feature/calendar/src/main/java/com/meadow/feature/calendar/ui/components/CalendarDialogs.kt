package com.meadow.feature.calendar.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meadow.core.ui.components.MeadowDialog
import com.meadow.core.ui.components.MeadowDialogType
import com.meadow.feature.calendar.R

@Composable
fun CalendarDeleteConfirmDialog(
    eventTitle: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialog(
        type = MeadowDialogType.Alert,
        title = stringResource(R.string.delete_event_title),
        message = stringResource(
            R.string.delete_event_message,
            eventTitle
        ),
        onConfirm = onConfirmDelete,
        onDismiss = onDismiss
    )
}