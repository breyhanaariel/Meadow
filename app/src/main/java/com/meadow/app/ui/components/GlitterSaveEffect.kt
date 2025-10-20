package com.meadow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawCircle

/**
 * GlitterSaveEffect.kt
 *
 * A gentle glitter animation shown when saving/syncing occurs.
 */

@Composable
fun GlitterSaveEffect(
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "glitter")
    val shimmer = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val alpha = shimmer.value
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = alpha),
            radius = size.minDimension * 0.1f
        )
    }
}
