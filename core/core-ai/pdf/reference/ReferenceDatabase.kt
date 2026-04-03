package com.meadow.core.ai.pdf.reference

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ReferenceDocumentEntity::class,
        ReferenceChunkEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ReferenceEmbeddingConverters::class)
abstract class ReferenceDatabase : RoomDatabase() {
    abstract fun referenceDao(): ReferenceDao
}
