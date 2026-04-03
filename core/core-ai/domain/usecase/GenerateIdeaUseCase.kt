package com.meadow.core.ai.domain.usecase

import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona

class GenerateIdeaUseCase(
    private val aiRepository: AiRepositoryContract
) {
    suspend operator fun invoke(prompt: String) =
        aiRepository.generateResponse(
            persona = AiPersona.Sprout,
            input = prompt
        )
}