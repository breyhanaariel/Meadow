package com.meadow.app.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * WorkManagerModule.kt
 *
 * Integrates Hilt with WorkManager so @HiltWorker works.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(workerFactory: HiltWorkerFactory): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}