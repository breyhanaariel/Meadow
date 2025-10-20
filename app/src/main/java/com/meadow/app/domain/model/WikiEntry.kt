package com.meadow.app.domain.models

/**
 * WikiEntry.kt
 *
 * Represents a single entry in the user's project wiki.
 */

data class WikiEntry(
    val id: String = "",
    val projectId: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "General",
    val lastEdited: Long = System.currentTimeMillis(),
    val relatedIds: List<String> = emptyList() // crosslinks to other catalog or wiki items
)