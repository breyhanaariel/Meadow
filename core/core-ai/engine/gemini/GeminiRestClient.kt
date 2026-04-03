package com.meadow.core.ai.engine.gemini

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object GeminiRestClient {

    private const val BASE_URL =
        "https://generativelanguage.googleapis.com/v1"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    private val client = OkHttpClient()

    suspend fun generateText(
        apiKey: String,
        model: String,
        prompt: String,
        temperature: Float = 0.7f,
        maxTokens: Int = 2048
    ): String = withContext(Dispatchers.IO) {

        val requestBody = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt)
                    )
                )
            ),
            generationConfig = GenerationConfig(
                temperature = temperature,
                maxOutputTokens = maxTokens
            )
        )

        val body = json
            .encodeToString(requestBody)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/$model:generateContent?key=$apiKey")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException(
                    "Gemini API error ${response.code}: ${response.body?.string()}"
                )
            }

            val raw = response.body?.string().orEmpty()
            val parsed = json.decodeFromString<GeminiResponse>(raw)

            parsed.candidates
                .firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                .orEmpty()
        }
    }
}
