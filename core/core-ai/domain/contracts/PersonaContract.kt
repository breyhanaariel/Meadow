package com.meadow.core.ai.domain.contracts

import com.meadow.core.ai.domain.model.AiPersona

interface PersonaContract {
    val persona: AiPersona
    fun buildPrompt(userInput: String, extraContext: String? = null): String
}