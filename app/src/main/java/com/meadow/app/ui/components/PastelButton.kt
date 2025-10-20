package com.meadow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color

/**
 * PastelButton.kt
 *
 * Reusable button styled in soft pastel gradients
 * for the Meadow aesthetic.
 */

@Composable
fun PastelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(50.dp),
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFE6E6FA), Color(0xFFD8BFD8))
                ),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF4B0082)
        )
    }
}
