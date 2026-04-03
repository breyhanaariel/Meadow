package com.meadow.core.ai.engine.personas.petal

import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona

class PetalUseCases(
    private val aiRepository: AiRepositoryContract,
    private val contextRepo: AiContextRepository
) {

    suspend fun critique(
        text: String,
        notes: String? = null
    ) = aiRepository.generateResponse(
        persona = AiPersona.Petal,
        input = text,
        extraContext = notes
    )

    suspend fun critiqueLongChapter(
        chapterText: String,
        authorNotes: String? = null
    ): PetalCritiqueResult {

        val chunks = PetalChunker.chunk(chapterText)
        val responses = mutableListOf<String>()

        for ((index, chunk) in chunks.withIndex()) {

            val context = buildString {
                append("This is section ${index + 1} of ${chunks.size} of the author's chapter.")
                if (!authorNotes.isNullOrBlank()) {
                    append("\n\nAuthor notes:\n$authorNotes")
                }
            }

            val result = aiRepository.generateResponse(
                persona = AiPersona.Petal,
                input = chunk,
                extraContext = context
            )

            responses.add(result.content.trim())
        }

        val combined = buildString {
            responses.forEachIndexed { i, critique ->
                appendLine("=== Petal Critique Section ${i + 1} ===")
                appendLine(critique)
                appendLine()
            }
        }.trim()

        return PetalCritiqueResult(
            combinedCritique = combined,
            chunkCritiques = responses
        )
    }

    private suspend fun buildContextBlock(scopeKey: String?): String {
        val contexts = contextRepo.resolveInjectedContexts(
            persona = AiPersona.Petal,
            scopeKey = scopeKey,
            includeScoped = true
        )
        return contextRepo.buildContextBlock(contexts)
    }
}

data class PetalCritiqueResult(
    val combinedCritique: String,
    val chunkCritiques: List<String>
)
