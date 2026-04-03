package com.meadow.feature.timeline.model

data class TimelineEvent(
    val id: String,
    val projectId: String,
    val title: String,
    val timestamp: Long
)
