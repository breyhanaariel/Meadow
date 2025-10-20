package com.meadow.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Theme.kt
 *
 * Combines Meadow’s pastel palette, typography, and shapes into a single theme.
 * This theme is wrapped around all screens using MeadowTheme { ... }.
 */

private val LightColorScheme = lightColorScheme(
    primary = PastelPurple,
    secondary = PastelPink,
    tertiary = PastelMint,
    background = PastelCream,
    surface = SoftGray
)

@Composable
fun MeadowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = MeadowTypography,
        shapes = MeadowShapes,
        content = content
    )
}
