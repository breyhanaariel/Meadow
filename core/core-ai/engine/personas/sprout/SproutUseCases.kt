package com.meadow.core.ai.engine.personas.sprout

import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona

class SproutUseCases(
    private val aiRepository: AiRepositoryContract,
    private val contextRepo: AiContextRepository
) {
    suspend fun generateIdea(
        prompt: String,
        scopeKey: String?
    ) = aiRepository.generateResponse(
        persona = AiPersona.Sprout,
        input = prompt,
        extraContext = buildContextBlock(scopeKey)
    )

    private suspend fun buildContextBlock(scopeKey: String?): String {
        val contexts = contextRepo.resolveInjectedContexts(
            persona = AiPersona.Sprout,
            scopeKey = scopeKey,
            includeScoped = true
        )
        return contextRepo.buildContextBlock(contexts)
    }
}