package com.meadow.app.domain.models

/**
 * TimelineEvent.kt
 *
 * Represents a single event in a project timeline.
 */

data class TimelineEvent(
    val id: String = "",
    val projectId: String = "",
    val title: String = "",
    val description: String = "",
    val date: String? = null,
    val linkedScene: String? = null,
    val tags: List<String> = emptyList(),
    val isMajor: Boolean = false
)