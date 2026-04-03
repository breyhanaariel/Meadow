package com.meadow.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meadow.core.ai.data.chat.AiChatDao
import com.meadow.core.ai.data.chat.AiChatEntity
import com.meadow.core.ai.data.chat.AiMessageEntity
import com.meadow.core.ai.data.context.AiContextDao
import com.meadow.core.ai.data.context.AiContextEntity
import com.meadow.core.ai.pdf.PdfBookmarkDao
import com.meadow.core.ai.pdf.PdfBookmarkEntity
import com.meadow.core.ai.pdf.reference.ReferenceDao
import com.meadow.core.ai.pdf.reference.ReferenceChunkEntity
import com.meadow.core.ai.pdf.reference.ReferenceDocumentEntity
import com.meadow.core.ai.pdf.reference.ReferenceEmbeddingConverters
import com.meadow.core.database.history.HistoryDao
import com.meadow.core.database.history.HistoryEntity
import com.meadow.core.node.data.NodeDao
import com.meadow.core.node.data.NodeEdgeDao
import com.meadow.core.node.data.NodeEdgeEntity
import com.meadow.core.node.data.NodeEntity
import com.meadow.feature.calendar.data.local.CalendarDao
import com.meadow.feature.calendar.data.local.CalendarEventEntity
import com.meadow.feature.catalog.data.local.CatalogFieldValueDao
import com.meadow.feature.catalog.data.local.CatalogFieldValueEntity
import com.meadow.feature.catalog.data.local.CatalogItemDao
import com.meadow.feature.catalog.data.local.CatalogItemEntity
import com.meadow.feature.catalog.data.local.CatalogLinkDao
import com.meadow.feature.catalog.data.local.CatalogLinkEntity
import com.meadow.feature.project.data.local.ProjectDao
import com.meadow.feature.project.data.local.ProjectEntity
import com.meadow.feature.project.data.local.ProjectFieldValueDao
import com.meadow.feature.project.data.local.ProjectFieldValueEntity
import com.meadow.feature.project.data.local.SeriesDao
import com.meadow.feature.project.data.local.SeriesEntity
import com.meadow.feature.script.data.local.ScriptAssetDao
import com.meadow.feature.script.data.local.ScriptAssetEntity
import com.meadow.feature.script.data.local.ScriptBlockAnchorDao
import com.meadow.feature.script.data.local.ScriptBlockAnchorEntity
import com.meadow.feature.script.data.local.ScriptCatalogLinkDao
import com.meadow.feature.script.data.local.ScriptCatalogLinkEntity
import com.meadow.feature.script.data.local.ScriptDao
import com.meadow.feature.script.data.local.ScriptEntity
import com.meadow.feature.script.data.local.ScriptMediaTrackDao
import com.meadow.feature.script.data.local.ScriptMediaTrackEntity
import com.meadow.feature.script.data.local.ScriptVariantDao
import com.meadow.feature.script.data.local.ScriptVariantEntity
import com.meadow.feature.script.data.local.SeasonDao
import com.meadow.feature.script.data.local.SeasonEntity
import com.meadow.feature.script.data.local.TranslationBlockStatusDao
import com.meadow.feature.script.data.local.TranslationBlockStatusEntity

@Database(
    entities = [
        // Project
        ProjectEntity::class,
        SeriesEntity::class,
        ProjectFieldValueEntity::class,

        // Catalog
        CatalogItemEntity::class,
        CatalogFieldValueEntity::class,
        CatalogLinkEntity::class,

        // Script
        ScriptEntity::class,
        SeasonEntity::class,
        ScriptVariantEntity::class,
        ScriptBlockAnchorEntity::class,
        ScriptCatalogLinkEntity::class,
        TranslationBlockStatusEntity::class,
        ScriptAssetEntity::class,
        ScriptMediaTrackEntity::class,

        // Calendar
        CalendarEventEntity::class,

        // Node
        NodeEntity::class,
        NodeEdgeEntity::class,

        // AI
        AiContextEntity::class,
        AiChatEntity::class,
        AiMessageEntity::class,
        PdfBookmarkEntity::class,
        ReferenceDocumentEntity::class,
        ReferenceChunkEntity::class,

        // History
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    ReferenceEmbeddingConverters::class
)
abstract class MeadowDatabase : RoomDatabase() {

    // Project
    abstract fun projectDao(): ProjectDao
    abstract fun seriesDao(): SeriesDao
    abstract fun projectFieldValueDao(): ProjectFieldValueDao

    // Catalog
    abstract fun catalogItemDao(): CatalogItemDao
    abstract fun catalogFieldValueDao(): CatalogFieldValueDao
    abstract fun catalogLinkDao(): CatalogLinkDao

    // Script
    abstract fun scriptDao(): ScriptDao
    abstract fun seasonDao(): SeasonDao
    abstract fun scriptVariantDao(): ScriptVariantDao
    abstract fun scriptBlockAnchorDao(): ScriptBlockAnchorDao
    abstract fun scriptCatalogLinkDao(): ScriptCatalogLinkDao
    abstract fun translationBlockStatusDao(): TranslationBlockStatusDao
    abstract fun scriptAssetDao(): ScriptAssetDao
    abstract fun scriptMediaTrackDao(): ScriptMediaTrackDao

    // Calendar
    abstract fun calendarDao(): CalendarDao

    // Node
    abstract fun nodeDao(): NodeDao
    abstract fun nodeEdgeDao(): NodeEdgeDao

    // AI
    abstract fun contextDao(): AiContextDao
    abstract fun chatDao(): AiChatDao
    abstract fun bookmarkDao(): PdfBookmarkDao
    abstract fun referenceDao(): ReferenceDao

    // History
    abstract fun historyDao(): HistoryDao
}