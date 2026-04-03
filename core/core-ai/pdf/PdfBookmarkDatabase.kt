package com.meadow.core.ai.pdf

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PdfBookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PdfBookmarkDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): PdfBookmarkDao
}
