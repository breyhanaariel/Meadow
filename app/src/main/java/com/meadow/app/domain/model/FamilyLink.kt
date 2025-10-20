package com.meadow.app.domain.model

/**
 * FamilyLink.kt
 *
 * Defines a character-to-character relationship
 * (e.g. Parent, Sibling, Partner, Rival, etc.)
 */

data class FamilyLink(
    val id: String,
    val projectId: String,
    val characterAId: String,
    val characterBId: String,
    val relationshipType: String
)
