package com.meadow.core.ai.domain.model

data class AiPromptTemplate(
    val persona: AiPersona,
    val name: String,
    val text: String
)