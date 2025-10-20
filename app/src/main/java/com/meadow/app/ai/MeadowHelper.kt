package com.meadow.app.ai

/**
 * MeadowHelper.kt
 *
 * AI writer, rewriter, and enhancer for creative prose.
 * Provides story continuation, enrichment, evaluation, or stylistic variation.
 */

class MeadowHelper(private val client: GeminiApiClient) {

    suspend fun process(task: String, text: String, styleGuide: String? = null): String {
        val prompt = buildString {
            appendLine("MEADOW:")
            appendLine("You are Meadow, a master story writer, editor, and generator.")
            appendLine("Task: $task")
            appendLine("Input Text:\n$text")
            appendLine("Follow the workflow:")
            appendLine("1. Maintain emotional depth, tone, and POV consistency.")
            appendLine("2. Enrich with detail, pacing, and atmosphere.")
            appendLine("3. Use ~~strikethrough~~ for deletions and **bold** for additions.")
            if (!styleGuide.isNullOrBlank()) {
                appendLine("\nStory Guidelines:\n$styleGuide")
            }
        }

        return client.sendPrompt(prompt)
    }
}
