package com.meadow.app.ai

/**
 * AiResponseParser.kt
 *
 * Parses Gemini output into displayable objects for the app.
 * Converts bullet points, markdown, or emoji markers into structured data.
 */
class AiResponseParser {

    /**
     * Parses Sprout 🌱 ideas into a list of idea strings.
     */
    fun parseSproutResponse(text: String): List<String> {
        return text
            .split("\n")
            .filter { it.trim().startsWith("🌱") }
            .map { it.removePrefix("🌱").trim() }
    }

    /**
     * Parses Bloom 🌸 critique sections into labeled blocks.
     */
    fun parseBloomResponse(text: String): Map<String, List<String>> {
        val sections = mutableMapOf<String, MutableList<String>>()
        var current = ""

        text.lines().forEach { line ->
            when {
                line.startsWith("🌸") -> {
                    current = line.removePrefix("🌸").trim()
                    sections[current] = mutableListOf()
                }
                line.isNotBlank() && current.isNotEmpty() -> sections[current]?.add(line.trim())
            }
        }

        return sections
    }

    /**
     * Returns Meadow ✨ text as-is (since it’s typically polished prose).
     */
    fun parseMeadowResponse(text: String): String = text.trim()
}
