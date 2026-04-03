package com.meadow.app.di

import com.meadow.app.logging.LoggerImpl
import com.meadow.core.utils.logging.LoggerContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {

    @Binds
    @Singleton
    abstract fun bindLogger(impl: LoggerImpl): LoggerContract
}