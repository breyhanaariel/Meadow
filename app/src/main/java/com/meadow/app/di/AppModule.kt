package com.meadow.app.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import dagger.hilt.EntryPoint

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /* ─── PROVIDES APPLICATION CONTEXT ───────────────────── */
    @Provides
    @Singleton
    fun provideAppContext(
        @ApplicationContext context: Context
    ): Context = context


    /* ─── Provides Coroutine Dispatchers ───────────────────── */
    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default


    /* ─── Provides SharedPreferences ───────────────────── */
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences("meadow_prefs", Context.MODE_PRIVATE)

}