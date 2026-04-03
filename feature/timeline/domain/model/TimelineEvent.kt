package com.meadow.feature.timeline.domain.model

data class TimelineEvent(
    val id: String,
    val projectId: String,
    val title: String,
    val timestamp: Long
)
