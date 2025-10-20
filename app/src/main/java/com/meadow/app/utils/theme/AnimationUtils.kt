package com.meadow.app.utils.theme

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberInfiniteTransition

/**
 * AnimationUtils.kt
 *
 * Provides reusable infinite shimmer or glitter animations.
 */
object AnimationUtils {

    @Composable
    fun shimmerAlpha(): Float {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val alpha by transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 1200, easing = LinearEasing),
                RepeatMode.Reverse
            ),
            label = "alpha"
        )
        return alpha
    }
}
