package com.meadow.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.meadow.R

/**
 * UnicornAssistantButton.kt
 *
 * Floating pastel unicorn button that opens the AI assistant panel.
 * Positioned near navigation drawer or bottom corner.
 */

@Composable
fun UnicornAssistantButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(8.dp, CircleShape)
            .background(Color(0xFFF8E9FF), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_unicorn),
            contentDescription = "Unicorn Assistant",
            modifier = Modifier.size(36.dp)
        )
    }
}
