package com.meadow.app.utils.text

/**
 * GrammarUtils.kt
 *
 * Basic grammar and spelling utility placeholder.
 * Can later call Gemini or local libraries for corrections.
 */
object GrammarUtils {

    private val commonErrors = mapOf(
        "teh" to "the",
        "recieve" to "receive",
        "definately" to "definitely"
    )

    fun correctBasic(text: String): String {
        var corrected = text
        commonErrors.forEach { (wrong, right) ->
            corrected = corrected.replace(Regex("\\b$wrong\\b", RegexOption.IGNORE_CASE), right)
        }
        return corrected
    }

    fun wordCount(text: String): Int = text.trim().split("\\s+".toRegex()).size
}
