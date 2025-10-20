package com.meadow.app.ai

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

/**
 * GeminiApiClient.kt
 *
 * Core client for sending text prompts to the Gemini API.
 * Uses OkHttp for HTTP POST requests and Kotlin coroutines for async handling.
 */

class GeminiApiClient(private val apiKey: String) {
    private val client = OkHttpClient()
    private val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"

    suspend fun sendPrompt(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject()
                .put("contents", listOf(JSONObject().put("parts", listOf(JSONObject().put("text", prompt)))))

            val request = Request.Builder()
                .url("$endpoint?key=$apiKey")
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody.toString()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body()?.string()

            if (!response.isSuccessful || body == null) {
                Log.e("GeminiApiClient", "API Error: $body")
                return@withContext "Error: Unable to generate response."
            }

            val jsonResponse = JSONObject(body)
            jsonResponse.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text", "No text generated.") ?: "Empty response."
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "Exception: ${e.message}")
            "Error: ${e.localizedMessage}"
        }
    }
}
