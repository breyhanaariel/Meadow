package com.meadow.core.ai.domain.model

data class AiResponse(
    val persona: AiPersona,
    val prompt: String,
    val content: String
)