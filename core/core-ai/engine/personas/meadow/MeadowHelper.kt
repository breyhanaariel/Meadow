package com.meadow.core.ai.engine.personas.meadow

import com.meadow.core.ai.domain.contracts.PersonaContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.PdfSearchResult
import com.meadow.core.ai.R

class MeadowHelper(
    private val templateText: String,
    private val strings: (Int) -> String
) : PersonaContract {

    override val persona: AiPersona = AiPersona.Meadow

    fun buildPromptWithPdfResults(
        question: String,
        searchResults: List<PdfSearchResult>
    ): String {

        val context = buildString {
            searchResults.forEach { result ->
                appendLine(strings(R.string.ai_meadow_source) + ": ${result.documentTitle}")
                appendLine(strings(R.string.ai_meadow_page) + ": ${result.pageNumber}")
                appendLine(strings(R.string.ai_meadow_excerpt) + ":")
                appendLine(result.snippet)
                appendLine()
            }
        }

        return buildPrompt(question, context)
    }

    override fun buildPrompt(userInput: String, extraContext: String?): String {
        return buildString {
            appendLine(templateText)
            appendLine()

            appendLine(strings(R.string.ai_meadow_user_question) + ":")
            appendLine(userInput)

            extraContext?.takeIf { it.isNotBlank() }?.let {
                appendLine()
                appendLine(strings(R.string.ai_meadow_relevant_excerpts) + ":")
                appendLine(it)
            }
        }
    }
}
