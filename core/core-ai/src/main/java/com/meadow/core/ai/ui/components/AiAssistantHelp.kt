package com.meadow.core.ai.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.components.*
import com.meadow.core.ai.R

/* ─── AI ASSISTANT HELP COMPOSABLE ───────────────────── */

@Composable
fun AiAssistantHelp(modifier: Modifier = Modifier) {

    /* ─── UI STATE ───────────────────── */
    /* Controls visibility of the help dialog */
    var showDialog by remember { mutableStateOf(false) }

    /* ─── TOOLTIP ───────────────────── */
    /* Passive helper tooltip explaining the AI assistant */
    MeadowTooltip(
        text = stringResource(R.string.ai_help_tooltip),
        modifier = modifier.padding(4.dp)
    )

    /* ─── HELP BUTTON ───────────────────── */
    /* Explicit action to open the AI help dialog */
    TextButton(onClick = { showDialog = true }) {
        Text(stringResource(R.string.ai_help_button))
    }

    /* ─── HELP DIALOG ───────────────────── */
    /* Modal dialog providing detailed AI assistant explanation */
    if (showDialog) {
        MeadowDialog(
            type = MeadowDialogType.Info,
            title = stringResource(R.string.ai_help_title),
            message = stringResource(R.string.ai_help_description),
            onConfirm = { showDialog = false },
            onDismiss = { showDialog = false }
        )
    }
}
