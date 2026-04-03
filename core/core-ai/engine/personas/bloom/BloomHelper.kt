package com.meadow.core.ai.engine.personas.bloom

import com.meadow.core.ai.domain.contracts.PersonaContract
import com.meadow.core.ai.domain.model.AiPersona

class BloomHelper(
    private val templateText: String,
    private val keyStrings: (String) -> String
) : PersonaContract {

    override val persona: AiPersona = AiPersona.Bloom

    override fun buildPrompt(userInput: String, extraContext: String?): String {
        return buildString {
            appendLine(templateText)
            appendLine()

            appendLine(keyStrings("ai_bloom_original_text") + ":")
            appendLine(userInput)

            extraContext?.takeIf { it.isNotBlank() }?.let {
                appendLine()
                appendLine(keyStrings("ai_bloom_editing_instructions") + ":")
                appendLine(it)
            }
        }
    }
}
