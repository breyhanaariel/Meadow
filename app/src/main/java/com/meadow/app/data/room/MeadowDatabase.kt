package com.meadow.app.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.meadow.app.data.room.dao.*
import com.meadow.app.data.room.entities.*

@Database(
    entities = [
        ProjectEntity::class,
        ScriptEntity::class,
        ScriptVersionEntity::class,
        CatalogItemEntity::class,
        WikiEntryEntity::class,
        TimelineEventEntity::class,
        CalendarEventEntity::class,
        StoryboardEntity::class,
        MindMapNodeEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MeadowDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun scriptDao(): ScriptDao
    abstract fun scriptVersionDao(): ScriptVersionDao
    abstract fun catalogDao(): CatalogDao
    abstract fun wikiDao(): WikiDao
    abstract fun timelineDao(): TimelineDao
    abstract fun calendarDao(): CalendarDao
    abstract fun storyboardDao(): StoryboardDao
    abstract fun mindMapDao(): MindMapDao
}
