package com.meadow.app.di

import android.content.Context
import androidx.room.Room
import com.meadow.app.database.MeadowDatabase
import com.meadow.core.ai.data.chat.AiChatDao
import com.meadow.core.ai.data.context.AiContextDao
import com.meadow.core.ai.pdf.PdfBookmarkDao
import com.meadow.core.ai.pdf.reference.ReferenceDao
import com.meadow.core.database.history.HistoryDao
import com.meadow.core.node.data.NodeDao
import com.meadow.core.node.data.NodeEdgeDao
import com.meadow.feature.calendar.data.local.CalendarDao
import com.meadow.feature.catalog.data.local.CatalogFieldValueDao
import com.meadow.feature.catalog.data.local.CatalogItemDao
import com.meadow.feature.catalog.data.local.CatalogLinkDao
import com.meadow.feature.project.data.local.ProjectDao
import com.meadow.feature.project.data.local.ProjectFieldValueDao
import com.meadow.feature.project.data.local.SeriesDao
import com.meadow.feature.script.data.local.ScriptAssetDao
import com.meadow.feature.script.data.local.ScriptBlockAnchorDao
import com.meadow.feature.script.data.local.ScriptCatalogLinkDao
import com.meadow.feature.script.data.local.ScriptDao
import com.meadow.feature.script.data.local.ScriptMediaTrackDao
import com.meadow.feature.script.data.local.ScriptVariantDao
import com.meadow.feature.script.data.local.SeasonDao
import com.meadow.feature.script.data.local.TranslationBlockStatusDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMeadowDatabase(
        @ApplicationContext context: Context
    ): MeadowDatabase =
        Room.databaseBuilder(
            context,
            MeadowDatabase::class.java,
            "meadow.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    // Project
    @Provides
    fun provideProjectDao(db: MeadowDatabase): ProjectDao =
        db.projectDao()

    @Provides
    fun provideSeriesDao(db: MeadowDatabase): SeriesDao =
        db.seriesDao()

    @Provides
    fun provideProjectFieldValueDao(db: MeadowDatabase): ProjectFieldValueDao =
        db.projectFieldValueDao()

    // Catalog
    @Provides
    fun provideCatalogItemDao(db: MeadowDatabase): CatalogItemDao =
        db.catalogItemDao()

    @Provides
    fun provideCatalogFieldValueDao(db: MeadowDatabase): CatalogFieldValueDao =
        db.catalogFieldValueDao()

    @Provides
    fun provideCatalogLinkDao(db: MeadowDatabase): CatalogLinkDao =
        db.catalogLinkDao()

    // Script
    @Provides
    fun provideScriptDao(db: MeadowDatabase): ScriptDao =
        db.scriptDao()

    @Provides
    fun provideSeasonDao(db: MeadowDatabase): SeasonDao =
        db.seasonDao()

    @Provides
    fun provideScriptVariantDao(db: MeadowDatabase): ScriptVariantDao =
        db.scriptVariantDao()

    @Provides
    fun provideScriptBlockAnchorDao(db: MeadowDatabase): ScriptBlockAnchorDao =
        db.scriptBlockAnchorDao()

    @Provides
    fun provideScriptCatalogLinkDao(db: MeadowDatabase): ScriptCatalogLinkDao =
        db.scriptCatalogLinkDao()

    @Provides
    fun provideTranslationBlockStatusDao(db: MeadowDatabase): TranslationBlockStatusDao =
        db.translationBlockStatusDao()

    @Provides
    fun provideScriptAssetDao(db: MeadowDatabase): ScriptAssetDao =
        db.scriptAssetDao()

    @Provides
    fun provideScriptMediaTrackDao(db: MeadowDatabase): ScriptMediaTrackDao =
        db.scriptMediaTrackDao()

    // Calendar
    @Provides
    fun provideCalendarDao(db: MeadowDatabase): CalendarDao =
        db.calendarDao()

    // Node
    @Provides
    fun provideNodeDao(db: MeadowDatabase): NodeDao =
        db.nodeDao()

    @Provides
    fun provideNodeEdgeDao(db: MeadowDatabase): NodeEdgeDao =
        db.nodeEdgeDao()

    // AI
    @Provides
    fun provideAiContextDao(db: MeadowDatabase): AiContextDao =
        db.contextDao()

    @Provides
    fun provideAiChatDao(db: MeadowDatabase): AiChatDao =
        db.chatDao()

    @Provides
    fun providePdfBookmarkDao(db: MeadowDatabase): PdfBookmarkDao =
        db.bookmarkDao()

    @Provides
    fun provideReferenceDao(db: MeadowDatabase): ReferenceDao =
        db.referenceDao()

    // History
    @Provides
    fun provideHistoryDao(db: MeadowDatabase): HistoryDao =
        db.historyDao()
}