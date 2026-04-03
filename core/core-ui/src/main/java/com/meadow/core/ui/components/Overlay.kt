package com.meadow.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R
import com.meadow.core.ui.components.tokens.ComponentRoles
import com.meadow.core.ui.theme.MeadowGradients

@Composable
fun MeadowOverlay(
    visible: Boolean,
    scrim: Color = Color.Black.copy(alpha = 0.45f),
    content: @Composable () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = stringResource(R.string.overlay_animation_label)
    )

    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scrim.copy(alpha = alpha)),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun MeadowHighlightOverlay(
    targetContent: @Composable () -> Unit,
    tip: @Composable () -> Unit = {
        Text(
            text = stringResource(R.string.overlay_default_tip),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    },
    scrim: Color = Color.Black.copy(alpha = 0.45f)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scrim),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(ComponentRoles.Shape.BubbleSoft)
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) { targetContent() }

        Spacer(modifier = Modifier.height(16.dp))
        tip()
    }
}

@Composable
fun MeadowFloatingPanel(
    visible: Boolean,
    gradient: Brush = MeadowGradients.FloatingGlow,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return

    Surface(
        shape = ComponentRoles.Shape.BubbleSoft,
        tonalElevation = ComponentRoles.Elevation.High,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { content() }
    }
}