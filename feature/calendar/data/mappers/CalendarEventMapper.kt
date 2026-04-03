package com.meadow.feature.calendar.data.mappers

import com.meadow.feature.calendar.data.local.CalendarEventEntity
import com.meadow.feature.calendar.domain.model.CalendarEvent

object CalendarEventMapper {

    private const val SEP = "\n"

    fun toDomain(e: CalendarEventEntity): CalendarEvent =
        CalendarEvent(
            localId = e.localId,
            googleEventId = e.googleEventId,
            googleCalendarId = e.googleCalendarId,
            title = e.title,
            description = e.description,
            location = e.location,
            startAtMillis = e.startAtMillis,
            endAtMillis = e.endAtMillis,
            allDay = e.allDay,
            timeZone = e.timeZone,
            colorId = e.colorId,
            recurrence = e.recurrenceRrules?.takeIf { it.isNotBlank() }?.split(SEP),
            reminderMinutes = e.reminderMinutes,
            scope = e.scope,
            projectId = e.projectId,
            seriesId = e.seriesId,
            isDeleted = e.isDeleted,
            localUpdatedAt = e.localUpdatedAt,
            remoteUpdatedAt = e.remoteUpdatedAt,
            etag = e.etag,
            isDirty = e.isDirty,
            hasConflict = e.hasConflict,
            lastSyncError = e.lastSyncError
        )

    fun toEntity(d: CalendarEvent): CalendarEventEntity =
        CalendarEventEntity(
            localId = d.localId,
            googleEventId = d.googleEventId,
            googleCalendarId = d.googleCalendarId,
            title = d.title,
            description = d.description,
            location = d.location,
            startAtMillis = d.startAtMillis,
            endAtMillis = d.endAtMillis,
            allDay = d.allDay,
            timeZone = d.timeZone,
            colorId = d.colorId,
            recurrenceRrules = d.recurrence?.joinToString(SEP),
            reminderMinutes = d.reminderMinutes,
            scope = d.scope,
            projectId = d.projectId,
            seriesId = d.seriesId,
            isDeleted = d.isDeleted,
            localUpdatedAt = d.localUpdatedAt,
            remoteUpdatedAt = d.remoteUpdatedAt,
            etag = d.etag,
            isDirty = d.isDirty,
            hasConflict = d.hasConflict,
            lastSyncError = d.lastSyncError
        )
}
