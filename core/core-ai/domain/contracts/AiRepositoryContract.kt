package com.meadow.core.ai.domain.contracts

import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.AiResponse

interface AiRepositoryContract {
    suspend fun generateResponse(
        persona: AiPersona,
        input: String,
        extraContext: String? = null
    ): AiResponse
}