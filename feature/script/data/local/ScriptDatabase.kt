package com.meadow.feature.script.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ScriptEntity::class,
        SeasonEntity::class,
        ScriptVariantEntity::class,
        ScriptBlockAnchorEntity::class,
        ScriptCatalogLinkEntity::class,
        TranslationBlockStatusEntity::class,
        ScriptAssetEntity::class,
        ScriptMediaTrackEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ScriptDatabase : RoomDatabase() {

    abstract fun scriptDao(): ScriptDao

    abstract fun seasonDao(): SeasonDao

    abstract fun scriptVariantDao(): ScriptVariantDao

    abstract fun scriptBlockAnchorDao(): ScriptBlockAnchorDao

    abstract fun scriptCatalogLinkDao(): ScriptCatalogLinkDao

    abstract fun translationBlockStatusDao(): TranslationBlockStatusDao

    abstract fun scriptAssetDao(): ScriptAssetDao

    abstract fun scriptMediaTrackDao(): ScriptMediaTrackDao
}
