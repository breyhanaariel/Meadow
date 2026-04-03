package com.meadow.core.ai.data.chat

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AiChatEntity::class, AiMessageEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AiChatDatabase : RoomDatabase() {
    abstract fun chatDao(): AiChatDao
}
