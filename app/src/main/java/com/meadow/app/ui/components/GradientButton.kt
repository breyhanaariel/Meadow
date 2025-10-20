package com.meadow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * GradientButton.kt
 *
 * A rounded pastel gradient button for primary actions.
 */
@Composable
fun GradientButton(
    text: String,
    gradientColors: List<Color> = listOf(Color(0xFFD9C7F0), Color(0xFFFADADD)),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(gradientColors),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clickableNoRipple { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

/**
 * Utility modifier to disable ripple effect for cleaner UI.
 */
@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.background(Color.Transparent).padding(0.dp)
    )
}
