package com.meadow.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LavenderScheme = lightColorScheme(
    primary = Color(0xFF6A4C93),
    secondary = Color(0xFFD7B7FF),
    background = Color(0xFFF8F5FF),
    surface = Color.White,
    onPrimary = Color.White,
)

private val PinkScheme = lightColorScheme(
    primary = Color(0xFFF6A5C0),
    secondary = Color(0xFFFFCFE2),
    background = Color(0xFFFFF4F8),
    surface = Color.White,
    onPrimary = Color.White,
)

private val MintScheme = lightColorScheme(
    primary = Color(0xFF7EE8C8),
    secondary = Color(0xFFB9FBE3),
    background = Color(0xFFF2FFFB),
    surface = Color.White,
    onPrimary = Color.White,
)

private val pastelThemes = listOf(LavenderScheme, PinkScheme, MintScheme)

@Composable
fun MeadowTheme(themeIndex: Int = 0, content: @Composable () -> Unit) {
    val colors = pastelThemes.getOrElse(themeIndex) { LavenderScheme }
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
