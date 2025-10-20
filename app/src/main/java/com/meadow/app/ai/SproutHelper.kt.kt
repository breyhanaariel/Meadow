package com.meadow.app.ai

/**
 * SproutHelper.kt
 *
 * Handles idea generation for story elements.
 * Category-specific prompt templates for Gemini.
 */

class `SproutHelper.kt`(private val client: GeminiApiClient) {

    suspend fun generateIdeas(category: String, topic: String): String {
        val prompt = """
            SPROUT:
            You are Sprout, an AI brainstorming assistant for creative writing.
            Your purpose is to generate a high volume of unique, creative ideas quickly.
            
            Generate ideas for: $category
            Topic: $topic
            
            Rules:
            1. Format: Use bulleted lists for all ideas with 🌱 as the bullet.
            2. Concise and direct, no fillers.
            3. Only generate the requested category at a time.
            4. Be highly creative, unusual, and original.
        """.trimIndent()

        return client.sendPrompt(prompt)
    }
}
