package com.meadow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meadow.app.ui.theme.PastelPink

/**
 * TooltipBubble.kt
 *
 * Shows a small rounded popup hint under a form field.
 */
@Composable
fun TooltipBubble(text: String) {
    Box(
        modifier = Modifier
            .background(PastelPink.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 14.sp
        )
    }
}
