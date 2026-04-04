package com.meadow.app.di

import com.meadow.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppSecretsModule {

    @Provides
    @Singleton
    @Named("gemini_key")
    fun provideGeminiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }
}