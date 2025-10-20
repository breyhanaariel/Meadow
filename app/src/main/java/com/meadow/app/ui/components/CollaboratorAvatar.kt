package com.meadow.app.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.meadow.app.ui.theme.*

/**
 * CollaboratorAvatar.kt
 *
 * Displays a circular collaborator avatar with online/offline sparkle.
 */

@Composable
fun CollaboratorAvatar(
    name: String,
    image: Painter?,
    isOnline: Boolean
) {
    Box(contentAlignment = Alignment.BottomEnd) {
        if (image != null) {
            Image(
                painter = image,
                contentDescription = name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PastelBlue)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PastelLavender),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1), color = TextPrimary)
            }
        }

        Canvas(modifier = Modifier.size(10.dp).offset(x = (-2).dp, y = (-2).dp)) {
            drawCircle(color = if (isOnline) PastelMint else Color.Gray)
        }
    }
}