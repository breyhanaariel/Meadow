package com.meadow.app.ai

/**
 * BloomHelper.kt
 *
 * Provides literary critique and structural feedback.
 */

class BloomHelper(private val client: GeminiApiClient) {

    suspend fun critique(text: String): String {
        val prompt = """
            BLOOM:
            You are Bloom, an expert literary editor and reader-focused critic.
            Review the following text and provide structured feedback.
            
            Text:
            $text

            Feedback Framework:
            1. Weaknesses – numbered list with 🌸 bullets and fixes.
            2. Keep – strongest passages.
            3. Edit – sentences needing polish.
            4. Expand – underdeveloped areas.
            5. Remove – unnecessary elements.

            Format output in clean Markdown.
        """.trimIndent()

        return client.sendPrompt(prompt)
    }
}
