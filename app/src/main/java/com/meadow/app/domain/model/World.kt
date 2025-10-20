package com.meadow.app.domain.models

/**
 * World.kt
 *
 * Defines a world, its lore, and population.
 */

data class World(
    val id: String = "",
    val projectId: String = "",
    val name: String = "",
    val description: String = "",
    val history: String = "",
    val lore: String = "",
    val maps: List<String> = emptyList(), // image file paths
    val inhabitants: String = ""
)