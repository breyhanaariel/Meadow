package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeline_event_table")
data class TimelineEventEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val description: String? = null,
    val occursAt: Long? = null,   // epoch millis
    val linksJson: String? = null // optional link refs
)
