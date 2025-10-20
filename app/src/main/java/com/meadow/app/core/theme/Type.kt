package com.meadow.app.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.meadow.R

/**
 * Type.kt
 *
 * Defines custom fonts and text styles.
 * Meadow uses a rounded “bubble” font and a soft script for headers.
 */

val BubbleFont = FontFamily(Font(R.font.meadow_bubble))
val ScriptFont = FontFamily(Font(R.font.pastel_script))

val MeadowTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = ScriptFont,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BubbleFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BubbleFont,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    )
)
