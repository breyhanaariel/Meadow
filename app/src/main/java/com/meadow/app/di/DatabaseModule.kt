package com.meadow.app.di

import android.content.Context
import androidx.room.Room
import com.meadow.app.data.room.MeadowDatabase
import com.meadow.app.data.room.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DatabaseModule.kt
 *
 * Provides Room database and DAO dependencies via Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): MeadowDatabase =
        Room.databaseBuilder(
            context,
            MeadowDatabase::class.java,
            "meadow_database.db"
        )
            .fallbackToDestructiveMigration() // Safe for early dev phases
            .build()

    @Provides fun provideProjectDao(db: MeadowDatabase): ProjectDao = db.projectDao()
    @Provides fun provideScriptDao(db: MeadowDatabase): ScriptDao = db.scriptDao()
    @Provides fun provideScriptVersionDao(db: MeadowDatabase): ScriptVersionDao = db.scriptVersionDao()
    @Provides fun provideCatalogDao(db: MeadowDatabase): CatalogDao = db.catalogDao()
    @Provides fun provideWikiDao(db: MeadowDatabase): WikiDao = db.wikiDao()
    @Provides fun provideTimelineDao(db: MeadowDatabase): TimelineDao = db.timelineDao()
    @Provides fun provideCalendarDao(db: MeadowDatabase): CalendarDao = db.calendarDao()
    @Provides fun provideStoryboardDao(db: MeadowDatabase): StoryboardDao = db.storyboardDao()
    @Provides fun provideMindMapDao(db: MeadowDatabase): MindMapDao = db.mindMapDao()
}
