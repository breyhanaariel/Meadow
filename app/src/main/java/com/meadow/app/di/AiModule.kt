package com.meadow.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.meadow.app.ai.AiResponseParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * AiModule.kt
 *
 * This Dagger Hilt module provides dependencies related to
 * the Gemini API integration for AI-powered features (Sprout, Bloom, Meadow).
 *
 * It sets up Retrofit with an interceptor for the Gemini API key,
 * registers Gson for JSON parsing, and provides instances of
 * AiResponseParser and AiPromptTemplates for app-wide use.
 */

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

    /**
     * Provides a configured Gson instance for JSON serialization and deserialization.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    /**
     * Provides an OkHttpClient that automatically adds the Gemini API key
     * to every outgoing HTTP request.
     *
     * The API key should be stored securely (e.g., local.properties or encrypted store).
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val apiKey = System.getenv("GEMINI_API_KEY") ?: "YOUR_GEMINI_API_KEY"
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val request: Request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(request)
            })
            .build()
    }

    /**
     * Provides a Retrofit instance configured for Gemini API endpoints.
     * Used for generating creative content, feedback, or rewriting.
     */
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provides a reusable AI response parser that converts structured Gemini text
     * (Sprout 🌱, Bloom 🌸, Meadow ✨) into composable-friendly data.
     */
    @Provides
    @Singleton
    fun provideAiResponseParser(): AiResponseParser = AiResponseParser()

    /**
     * Provides reusable prompt templates for Sprout, Bloom, and Meadow AI modes.
     */
    @Provides
    @Singleton
    fun provideAiPromptTemplates(): `AiPromptTemplates.kt` = AiPromptTemplates()
}
