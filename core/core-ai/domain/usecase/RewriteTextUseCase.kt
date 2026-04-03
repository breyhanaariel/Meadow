package com.meadow.core.ai.domain.usecase

import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona

class RewriteTextUseCase(
    private val aiRepository: AiRepositoryContract
) {
    suspend operator fun invoke(text: String, instructions: String? = null) =
        aiRepository.generateResponse(
            persona = AiPersona.Petal,
            input = text,
            extraContext = instructions
        )
}