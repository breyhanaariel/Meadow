package com.meadow.app.utils.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

/**
 * ThemeUtils.kt
 *
 * Handles dynamic pastel themes (lavender, mint, pink, peach, etc.)
 * and allows quick switching at runtime.
 */
object ThemeUtils {

    var currentTheme = mutableStateOf("lavender")

    private val palettes = mapOf(
        "lavender" to listOf(Color(0xFFEBD9FF), Color(0xFFD9C7F0)),
        "mint" to listOf(Color(0xFFD4F8E8), Color(0xFFB8E6CE)),
        "pink" to listOf(Color(0xFFFADADD), Color(0xFFF9C9D4)),
        "peach" to listOf(Color(0xFFFFE0B2), Color(0xFFFFCC80)),
        "blue" to listOf(Color(0xFFD0E7FF), Color(0xFFAECFFF))
    )

    fun getCurrentPalette(): List<Color> = palettes[currentTheme.value] ?: palettes["lavender"]!!

    fun setTheme(theme: String) {
        if (palettes.containsKey(theme)) {
            currentTheme.value = theme
        }
    }
}
