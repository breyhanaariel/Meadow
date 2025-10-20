package com.meadow.app.domain.usecase

import com.meadow.app.data.repository.AiRepository
import javax.inject.Inject

/**
 * GenerateIdeasUseCase.kt
 *
 * Wraps Gemini AI’s “Sprout” brainstorming mode.
 */

class GenerateIdeasUseCase @Inject constructor(
    private val aiRepo: AiRepository
) {
    suspend operator fun invoke(category: String, topic: String): String {
        val prompt = """
            SPROUT:
            Generate ${category.uppercase()} ideas for "$topic"
        """.trimIndent()
        return aiRepo.generateIdeas(prompt)
    }
}
