package com.meadow.core.ai.engine.gemini

import com.meadow.core.ai.domain.model.AiPersona
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiClient @Inject constructor(
    @Named("gemini_key") private val apiKey: String
) {

    suspend fun generateText(
        prompt: String,
        persona: AiPersona
    ): String {

        return GeminiRestClient.generateText(
            apiKey = apiKey,
            model = "models/gemini-2.0-flash",
            prompt = prompt,
            temperature = 0.8f
        )
    }
}
