package com.meadow.core.google.di

import com.meadow.core.google.engine.GoogleOAuthTokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleAuthModule {

    @Provides
    @Singleton
    fun provideGoogleOAuthTokenService(
        client: OkHttpClient
    ): GoogleOAuthTokenService =
        GoogleOAuthTokenService(client)
}
