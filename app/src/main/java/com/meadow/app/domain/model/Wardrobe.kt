package com.meadow.app.domain.models

/**
 * Wardrobe.kt
 *
 * Represents clothing or costumes for characters.
 */

data class Wardrobe(
    val id: String = "",
    val projectId: String = "",
    val name: String = "",
    val description: String = "",
    val photo: String? = null, // path to drawable or URL
    val usedInScenes: List<String> = emptyList(),
    val charactersUsedBy: List<String> = emptyList(),
    val comments: String = ""
)