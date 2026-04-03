package com.meadow.feature.catalog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_links")
data class CatalogLinkEntity(
    @PrimaryKey val id: String,
    val fromItemId: String,
    val toItemId: String,
    val linkType: String
)
