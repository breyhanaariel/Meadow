package com.meadow.core.ai.data.context

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AiContextEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AiContextDatabase : RoomDatabase() {
    abstract fun contextDao(): AiContextDao
}
