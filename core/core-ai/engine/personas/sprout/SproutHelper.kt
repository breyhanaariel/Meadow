package com.meadow.core.ai.engine.personas.sprout

import com.meadow.core.ai.domain.contracts.PersonaContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.R

class SproutHelper(
    private val templateText: String,
    private val strings: (Int) -> String
) : PersonaContract {

    override val persona: AiPersona = AiPersona.Sprout

    override fun buildPrompt(userInput: String, extraContext: String?): String {
        return buildString {
            appendLine(templateText)
            appendLine()

            appendLine(strings(R.string.ai_sprout_idea_request) + ":")
            appendLine(userInput)

            extraContext?.takeIf { it.isNotBlank() }?.let {
                appendLine()
                appendLine(strings(R.string.ai_sprout_extra_context) + ":")
                appendLine(it)
            }
        }
    }
}
