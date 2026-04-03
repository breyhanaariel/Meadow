package com.meadow.feature.catalog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CatalogItemEntity::class,
        CatalogFieldValueEntity::class,
        CatalogLinkEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class CatalogDatabase : RoomDatabase() {

    abstract fun catalogItemDao(): CatalogItemDao

    abstract fun catalogFieldValueDao(): CatalogFieldValueDao

    abstract fun catalogLinkDao(): CatalogLinkDao
}
