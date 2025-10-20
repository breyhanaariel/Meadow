package com.meadow.app.domain.model

/**
 * CatalogItem.kt
 *
 * A flexible representation of a creative asset
 * such as a character, prop, world, or location.
 */

data class CatalogItem(
    val id: String,
    val projectId: String,
    val type: String,
    val name: String,
    val description: String?,
    val imageUri: String?,
    val linkedItems: List<String>,
    val metadata: Map<String, String> // stores flexible form fields
)
