package com.meadow.core.ai.engine.personas.vine

import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona

class VineUseCases(
    private val aiRepository: AiRepositoryContract,
    private val contextRepo: AiContextRepository

) {

    suspend fun draft(
        seedText: String,
        context: String? = null
    ) = aiRepository.generateResponse(
        persona = AiPersona.Vine,
        input = seedText,
        extraContext = context
    )

    private suspend fun buildContextBlock(scopeKey: String?): String {
        val contexts = contextRepo.resolveInjectedContexts(
            persona = AiPersona.Vine,
            scopeKey = scopeKey,
            includeScoped = true
        )
        return contextRepo.buildContextBlock(contexts)
    }
}
