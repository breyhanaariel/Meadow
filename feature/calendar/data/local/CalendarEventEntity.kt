package com.meadow.feature.calendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meadow.feature.calendar.domain.model.CalendarScope

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val localId: String,
    val googleEventId: String?,
    val googleCalendarId: String,
    val title: String,
    val description: String?,
    val location: String?,
    val startAtMillis: Long,
    val endAtMillis: Long,
    val allDay: Boolean,
    val timeZone: String,
    val colorId: String?,
    val recurrenceRrules: String?,
    val reminderMinutes: Int?,
    val scope: CalendarScope,
    val projectId: String?,
    val seriesId: String?,
    val isDeleted: Boolean,
    val localUpdatedAt: Long,
    val remoteUpdatedAt: Long?,
    val etag: String?,
    val isDirty: Boolean,
    val hasConflict: Boolean,
    val lastSyncError: String?
)
