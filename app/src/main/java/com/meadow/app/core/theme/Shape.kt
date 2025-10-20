package com.meadow.app.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape.kt
 *
 * All corner radius and curvature settings for Meadow UI.
 * Rounded corners reinforce the "soft, friendly" brand style.
 */

val MeadowShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(32.dp)
)
