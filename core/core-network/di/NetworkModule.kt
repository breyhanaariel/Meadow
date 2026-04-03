package com.meadow.core.network.di

import com.google.gson.Gson
import com.meadow.core.network.api.*
import com.meadow.core.network.config.Endpoints
import com.meadow.core.network.interceptors.AuthInterceptor
import com.meadow.core.network.interceptors.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: LoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @MainRetrofit
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(Endpoints.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideGeminiApi(
        @MainRetrofit retrofit: Retrofit
    ): GeminiApiService =
        retrofit.create(GeminiApiService::class.java)

    @Provides
    @Singleton
    fun provideFirestoreApi(
        @MainRetrofit retrofit: Retrofit
    ): FirestoreApiService =
        retrofit.create(FirestoreApiService::class.java)

    @Provides
    @Singleton
    fun provideSyncApi(
        @MainRetrofit retrofit: Retrofit
    ): SyncApiService =
        retrofit.create(SyncApiService::class.java)
}
