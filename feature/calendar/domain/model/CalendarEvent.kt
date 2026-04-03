package com.meadow.feature.calendar.domain.model

import com.meadow.feature.calendar.domain.model.CalendarScope

data class CalendarEvent(
    val localId: String,
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
    val recurrence: List<String>?,
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