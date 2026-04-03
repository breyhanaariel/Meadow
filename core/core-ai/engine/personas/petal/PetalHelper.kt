package com.meadow.core.ai.engine.personas.petal

import com.meadow.core.ai.domain.contracts.PersonaContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.R

class PetalHelper(
    private val templateText: String,
    private val strings: (Int) -> String
) : PersonaContract {

    override val persona: AiPersona = AiPersona.Petal

    override fun buildPrompt(userInput: String, extraContext: String?): String {
        return buildString {
            appendLine(templateText)
            appendLine()

            appendLine(strings(R.string.ai_petal_chapter_to_critique) + ":")
            appendLine(userInput)

            extraContext?.takeIf { it.isNotBlank() }?.let {
                appendLine()
                appendLine(strings(R.string.ai_petal_author_notes) + ":")
                appendLine(it)
            }
        }
    }
}
