package com.meadow.core.ai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ui.components.MeadowLayout
import androidx.compose.ui.res.stringResource
import com.meadow.core.ai.R
import com.meadow.core.ai.viewmodel.AiChatViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import com.meadow.core.ai.domain.model.AiChatEvent
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.ui.components.MeadowButtonType

@Composable
fun AiSidePanel(
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {

    /* ─── SIDE PANEL WRAPPER ───────────────────── */
    /* Provides a fixed-width AI panel using Meadow layout system */
    MeadowLayout.SidePanel(
        width = 360.dp,
        onClose = onClose
    ) {

        /* ─── PANEL CONTENT ───────────────────── */
        /* Caller-provided AI UI content */
        content()
    }
}
