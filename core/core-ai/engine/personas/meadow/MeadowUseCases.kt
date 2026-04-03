package com.meadow.core.ai.engine.personas.meadow

import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.AiResponse
import com.meadow.core.ai.pdf.reference.ReferenceRetriever
import javax.inject.Inject

class MeadowUseCases @Inject constructor(
    private val aiRepository: AiRepositoryContract,
    private val referenceRetriever: ReferenceRetriever,
    private val contextRepo: AiContextRepository
) {

    suspend fun answerWithLibrary(
        question: String,
        scopeKey: String? = null,
        includePersonaContexts: Boolean = false
    ): AiResponse {

        val hits = referenceRetriever.retrieve(question = question, topK = 10)

        val ragContext = referenceRetriever.buildMeadowContext(
            hits = hits,
            maxExcerptCharsPerHit = 600,
            maxTotalChars = 3000
        )

        val personaContextBlock =
            if (includePersonaContexts) {
                val contexts = contextRepo.resolveInjectedContexts(
                    persona = AiPersona.Meadow,
                    scopeKey = scopeKey,
                    includeScoped = true
                )
                contextRepo.buildContextBlock(contexts)
            } else ""

        val finalContext = buildString {
            if (personaContextBlock.isNotBlank()) {
                appendLine(personaContextBlock)
                appendLine()
            }
            appendLine("RELEVANT EXCERPTS:")
            appendLine(ragContext)
            appendLine()
            appendLine(
                """
INSTRUCTIONS:
- Answer conversationally, but stay grounded in the references.
- Give exactly 3 concrete improvements / ideas.
- For each improvement, cite at least 1 source item number (e.g., (1), (3)).
- If NOT FOUND IN REFERENCES, say so and stop.
                """.trim()
            )
        }.trim()

        return aiRepository.generateResponse(
            persona = AiPersona.Meadow,
            input = question,
            extraContext = finalContext
        )
    }

    private suspend fun buildContextBlock(scopeKey: String?): String {
        val contexts = contextRepo.resolveInjectedContexts(
            persona = AiPersona.Meadow,
            scopeKey = scopeKey,
            includeScoped = true
        )
        return contextRepo.buildContextBlock(contexts)
    }
}
