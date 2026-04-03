package com.meadow.core.ai.data.repository

import com.meadow.core.ai.engine.personas.bloom.BloomHelper
import com.meadow.core.ai.engine.personas.meadow.MeadowHelper
import com.meadow.core.ai.engine.personas.petal.PetalHelper
import com.meadow.core.ai.engine.personas.sprout.SproutHelper
import com.meadow.core.ai.engine.personas.vine.VineHelper
import com.meadow.core.ai.engine.prompt.AiPromptProvider
import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.AiResponse
import com.meadow.core.ai.gemini.GeminiClient
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val promptProvider: AiPromptProvider,
    private val intStrings: (Int) -> String,
    private val keyStrings: (String) -> String,
    private val geminiClient: GeminiClient
) : AiRepositoryContract {

    override suspend fun generateResponse(
        persona: AiPersona,
        input: String,
        extraContext: String?
    ): AiResponse {

        val baseTemplate = promptProvider.getPrompt(persona)

        val prompt = when (persona) {
            AiPersona.Sprout -> SproutHelper(baseTemplate, intStrings)
                .buildPrompt(input, extraContext)

            AiPersona.Bloom -> BloomHelper(baseTemplate, keyStrings)
                .buildPrompt(input, extraContext)

            AiPersona.Petal -> PetalHelper(baseTemplate, intStrings)
                .buildPrompt(input, extraContext)

            AiPersona.Meadow -> MeadowHelper(baseTemplate, intStrings)
                .buildPrompt(input, extraContext)

            AiPersona.Vine ->
                VineHelper(baseTemplate)
                    .buildPrompt(input, extraContext)

            AiPersona.Bud ->
                error("Bud responses must be generated via BudUseCases, not AiRepository.generateResponse")

        }

        val output = geminiClient.generateText(prompt, persona)

        return AiResponse(
            persona = persona,
            prompt = prompt,
            content = output
        )
    }
}
