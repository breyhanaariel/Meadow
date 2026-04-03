package com.meadow.core.network.api

import com.meadow.core.network.models.AiResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-pro:generateContent")
    suspend fun generateContent(@Body request: Map<String, Any>): AiResponseDto
}