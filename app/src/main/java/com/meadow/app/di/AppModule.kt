package com.meadow.app.di

import android.content.Context
import com.meadow.core.AppConstants
import com.meadow.core.MeadowLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule.kt
 *
 * This Hilt module provides core app-level singletons
 * such as application context and utility classes.
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** Provides the application context across the entire app. */
    @Provides
    @Singleton
    fun provideAppContext(@ApplicationContext context: Context): Context = context

    /** Provides the custom Meadow logger utility globally. */
    @Provides
    @Singleton
    fun provideLogger(): MeadowLogger = MeadowLogger

    /** Example of providing constants (optional but helpful for injection). */
    @Provides
    fun provideAppName(): String = AppConstants.APP_NAME
}
