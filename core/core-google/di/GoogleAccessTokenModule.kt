package com.meadow.core.google.di

import com.meadow.core.google.engine.GoogleAuthManager
import com.meadow.core.network.interceptors.AccessTokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleAccessTokenModule {

    @Provides
    @Singleton
    fun provideAccessTokenProvider(
        googleAuthManager: GoogleAuthManager
    ): AccessTokenProvider =
        object : AccessTokenProvider {
            override fun getAccessToken(): String? =
                googleAuthManager.accessToken()
        }
}
