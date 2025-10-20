package com.meadow.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * GlitterOverlay.kt
 *
 * Simple animated sparkle overlay for glittery backgrounds.
 * Used for loading screens and save/sync feedback.
 */

@Composable
fun GlitterOverlay() {
    val sparklePositions = remember {
        List(40) { Pair(Random.nextFloat(), Random.nextFloat()) }
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        sparklePositions.forEach { (x, y) ->
            drawCircle(
                color = Color(0xFFFFE6FF),
                radius = Random.nextInt(2, 6).toFloat(),
                center = androidx.compose.ui.geometry.Offset(
                    size.width * x,
                    size.height * y
                )
            )
        }
    }
}
