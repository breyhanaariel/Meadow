package com.meadow.core.ai.domain.usecase

import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona

class CritiqueTextUseCase(
    private val aiRepository: AiRepositoryContract
) {
    suspend operator fun invoke(text: String, notes: String? = null) =
        aiRepository.generateResponse(
            persona = AiPersona.Bloom,
            input = text,
            extraContext = notes
        )
}