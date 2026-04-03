package com.meadow.core.ai.di

import android.content.Context
import androidx.room.Room
import com.meadow.core.ai.data.chat.AiChatDatabase
import com.meadow.core.ai.data.context.AiContextDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiStorageModule {

    @Provides
    @Singleton
    fun provideAiChatDatabase(@ApplicationContext ctx: Context): AiChatDatabase =
        Room.databaseBuilder(ctx, AiChatDatabase::class.java, "ai_chat_library.db")
            .build()

    @Provides
    @Singleton
    fun provideAiContextDatabase(@ApplicationContext ctx: Context): AiContextDatabase =
        Room.databaseBuilder(ctx, AiContextDatabase::class.java, "ai_context_library.db")
            .build()

    @Provides
    fun provideAiChatDao(db: AiChatDatabase) = db.chatDao()

    @Provides
    fun provideAiContextDao(db: AiContextDatabase) = db.contextDao()
}
