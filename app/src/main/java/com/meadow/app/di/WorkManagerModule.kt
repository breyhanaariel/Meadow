package com.meadow.app.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.meadow.core.utils.coroutines.CoroutineUtils
import com.meadow.core.utils.logging.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager {
        Logger.i("WorkManagerModule", "Providing WorkManager instance")
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWorkManagerConfig(factory: HiltWorkerFactory): Configuration {
        Logger.i("WorkManagerModule", "Configuring WorkManager with HiltWorkerFactory")
        return Configuration.Builder()
            .setWorkerFactory(factory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = CoroutineUtils.IO
}