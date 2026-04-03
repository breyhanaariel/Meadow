package com.meadow.core.ai.domain.usecase

import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.AiResponse
import javax.inject.Inject

class GenerateAiResponseUseCase @Inject constructor(
    private val aiRepository: AiRepositoryContract
) {
    suspend operator fun invoke(
        persona: AiPersona,
        input: String,
        extraContext: String? = null
    ) : AiResponse {
        return try {
            aiRepository.generateResponse(persona, input, extraContext)
        } catch (e: Exception) {
            AiResponse(
                persona = persona,
                prompt = "",
                content = "Error: ${e.localizedMessage}"
            )
        }
    }
}
