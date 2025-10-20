package com.meadow.app.utils.extensions

/**
 * StringExtensions.kt
 *
 * Adds helpful text utilities for cleaning, shortening, and formatting text.
 */
object StringExtensions {

    fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

    fun String.trimQuotes(): String = trim('"', '\'')

    fun String.abbreviate(maxLength: Int = 40): String =
        if (length > maxLength) substring(0, maxLength) + "…" else this

    fun String.cleanMarkdown(): String =
        replace(Regex("\\*\\*|__|\\*|_|~|`"), "")
}
