package com.meadow.app.data.repository

import com.meadow.app.ai.GeminiClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AiRepository.kt
 *
 * Connects ViewModels to the Gemini AI client.
 * Handles text generation, rewriting, and critique tasks.
 */

@Singleton
class AiRepository @Inject constructor(
    private val geminiClient: GeminiClient
) {

    suspend fun generateIdeas(prompt: String): String {
        return geminiClient.generateText(prompt)
    }

    suspend fun critiqueText(prompt: String): String {
        return geminiClient.generateText(prompt)
    }

    suspend fun rewriteText(prompt: String): String {
        return geminiClient.generateText(prompt)
    }
}
