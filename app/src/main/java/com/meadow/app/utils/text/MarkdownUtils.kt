package com.meadow.app.utils

/**
 * MarkdownUtils.kt
 *
 * Simplified parser/formatter for Markdown or Fountain syntax.
 * Supports inline bold, italics, underline, and highlight for the script editor.
 */

object MarkdownUtils {

    /**
     * Basic Fountain to HTML-like markup converter.
     * (For Jetpack Compose, you can render this via AnnotatedString.)
     */
    fun parseFountainToStyled(text: String): String {
        return text
            .replace("**", "<b>")
            .replace("__", "<i>")
            .replace("==", "<u>")
            .replace("<<", "<mark>")
            .replace(">>", "</mark>")
    }

    /**
     * Strip all Markdown/Fountain syntax, keeping plain text only.
     */
    fun stripFormatting(text: String): String {
        val regex = Regex("[*_`~]+")
        return text.replace(regex, "")
    }

    /**
     * Detect if text contains Fountain-style scene heading (e.g. "INT." or "EXT.")
     */
    fun isSceneHeading(line: String): Boolean {
        return line.startsWith("INT.") || line.startsWith("EXT.")
    }
}
