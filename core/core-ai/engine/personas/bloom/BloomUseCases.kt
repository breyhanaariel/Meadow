package com.meadow.core.ai.engine.personas.bloom

import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona
import bloom.BloomChunker

class BloomUseCases(
    private val aiRepository: AiRepositoryContract,
    private val contextRepo: AiContextRepository

) {
    suspend fun rewrite(text: String, instructions: String? = null) =
        aiRepository.generateResponse(
            persona = AiPersona.Bloom,
            input = text,
            extraContext = instructions
        )


    suspend fun rewriteLong(
        text: String,
        instructions: String? = null
    ): String {

        val chunks = BloomChunker.chunk(text)

        val rewrittenChunks = chunks.map { chunk ->
            aiRepository.generateResponse(
                persona = AiPersona.Bloom,
                input = chunk,
                extraContext = instructions
            ).content
        }

        val mergedText = BloomChunker.mergeForFinalPass(rewrittenChunks)

        val smoothed = aiRepository.generateResponse(
            persona = AiPersona.Bloom,
            input = mergedText,
            extraContext = """
                Perform a smoothing pass:
                - unify pacing
                - ensure transitions between sections feel seamless
                - maintain the author’s voice
                - no added scenes, no added plot points
            """.trimIndent()
        ).content

        return smoothed
    }

    private suspend fun buildContextBlock(scopeKey: String?): String {
        val contexts = contextRepo.resolveInjectedContexts(
            persona = AiPersona.Bloom,
            scopeKey = scopeKey,
            includeScoped = true
        )
        return contextRepo.buildContextBlock(contexts)
    }
}
