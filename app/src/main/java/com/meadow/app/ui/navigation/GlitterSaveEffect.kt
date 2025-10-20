package com.meadow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.random.Random

/**
 * GlitterSaveEffect.kt
 *
 * Adds sparkly glitter animation overlay for saves or sync completion ✨
 */
@Composable
fun GlitterSaveEffect(
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    if (!visible) return

    val transition = rememberInfiniteTransition(label = "glitter")
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    Canvas(modifier = modifier) {
        val sparkleCount = 30
        drawIntoCanvas { canvas ->
            repeat(sparkleCount) {
                val x = Random.nextFloat() * size.width
                val y = Random.nextFloat() * size.height
                val alpha = Random.nextFloat() * shimmer
                val color = listOf(
                    Color(0xFFFFC1E3),
                    Color(0xFFAEC6FF),
                    Color(0xFFFFE8A7)
                ).random()
                drawCircle(color.copy(alpha = alpha), radius = 2f, center = Offset(x, y))
            }
        }
    }
}