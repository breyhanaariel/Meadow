package com.meadow.core.ai.engine.personas.vine

import com.meadow.core.ai.domain.contracts.PersonaContract
import com.meadow.core.ai.domain.model.AiPersona

class VineHelper(
    private val templateText: String
) : PersonaContract {

    override val persona: AiPersona = AiPersona.Vine

    override fun buildPrompt(userInput: String, extraContext: String?): String {
        return buildString {
            appendLine(templateText)
            appendLine()

            appendLine("Draft from the following:")
            appendLine(userInput)

            extraContext?.takeIf { it.isNotBlank() }?.let {
                appendLine()
                appendLine("Additional context:")
                appendLine(it)
            }
        }
    }
}
