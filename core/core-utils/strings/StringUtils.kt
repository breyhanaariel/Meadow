package com.meadow.core.utils.strings

object StringUtils {
    fun sanitize(input: String): String =
        input.replace("[^A-Za-z0-9_\\- ]".toRegex(), "").trim()

    fun slugify(text: String): String =
        text.lowercase()
            .replace("[^a-z0-9]+".toRegex(), "-")
            .trim('-')

    fun capitalizeWords(text: String): String =
        text.split(" ").joinToString(" ") {
            it.lowercase().replaceFirstChar(Char::titlecase)
        }
}