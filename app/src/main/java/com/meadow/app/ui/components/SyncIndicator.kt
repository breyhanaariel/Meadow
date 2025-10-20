package com.meadow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.meadow.R

/**
 * SyncIndicator.kt
 *
 * Displays a small glittering star/cloud animation
 * when a sync or save operation completes.
 */

@Composable
fun SyncIndicator(isSyncing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync_glitter")

    // Gentle pulsing scale animation.
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSyncing) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(28.dp)
            .padding(4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sync),
            contentDescription = "Sync Indicator",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = scale, scaleY = scale)
        )
    }
}
