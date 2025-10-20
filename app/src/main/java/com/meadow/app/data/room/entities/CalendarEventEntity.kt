package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_event_table")
data class CalendarEventEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val date: Long?,                 // due date
    val repeatRule: String? = null,  // e.g. "DAILY", "WEEKLY", RFC-5545 later
    val reminderMinutes: Int? = null
)
