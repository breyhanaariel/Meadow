package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Unified catalog item (Character, World, Prop, Wardrobe, etc.)
 * Type + name + JSON payload for arbitrary fields.
 */
@Entity(tableName = "catalog_item_table")
data class CatalogItemEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val type: String,             // e.g., "Character", "World", "Prop", "FX", "Quest", etc.
    val name: String,
    val payloadJson: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
