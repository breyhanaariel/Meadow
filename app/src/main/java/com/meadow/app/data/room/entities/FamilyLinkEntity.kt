package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * FamilyLinkEntity.kt
 *
 * Defines relationships between characters (for family tree).
 */

@Entity(tableName = "family_links")
data class FamilyLinkEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val characterAId: String,
    val characterBId: String,
    val relationshipType: String // e.g. Parent, Sibling, Partner
)
