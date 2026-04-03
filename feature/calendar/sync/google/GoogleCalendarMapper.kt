package com.meadow.feature.calendar.sync.google

import com.meadow.core.google.api.calendar.model.EventDateTime
import com.meadow.core.google.api.calendar.model.EventReminderOverride
import com.meadow.core.google.api.calendar.model.EventReminders
import com.meadow.core.google.api.calendar.model.ExtendedProperties
import com.meadow.feature.calendar.domain.model.CalendarEvent
import com.meadow.feature.calendar.domain.model.CalendarScope
import com.meadow.feature.calendar.sync.google.GoogleCalendarKeys
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object GoogleCalendarMapper {

    private val rfc3339 = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val isoDate = DateTimeFormatter.ISO_LOCAL_DATE

    fun toGoogle(event: CalendarEvent): com.meadow.core.google.api.calendar.model.CalendarEvent {
        val tz = ZoneId.of(event.timeZone)

        val start = if (event.allDay) {
            val d = Instant.ofEpochMilli(event.startAtMillis).atZone(tz).toLocalDate()
            EventDateTime(date = isoDate.format(d))
        } else {
            val odt = Instant.ofEpochMilli(event.startAtMillis).atZone(tz).toOffsetDateTime()
            EventDateTime(dateTime = rfc3339.format(odt), timeZone = event.timeZone)
        }

        val end = if (event.allDay) {
            val d = Instant.ofEpochMilli(event.endAtMillis).atZone(tz).toLocalDate()
            EventDateTime(date = isoDate.format(d))
        } else {
            val odt = Instant.ofEpochMilli(event.endAtMillis).atZone(tz).toOffsetDateTime()
            EventDateTime(dateTime = rfc3339.format(odt), timeZone = event.timeZone)
        }

        val props = mapOf(
            GoogleCalendarKeys.KEY_LOCAL_ID to event.localId,
            GoogleCalendarKeys.KEY_SCOPE to event.scope.name,
            GoogleCalendarKeys.KEY_PROJECT_ID to (event.projectId ?: ""),
            GoogleCalendarKeys.KEY_SERIES_ID to (event.seriesId ?: "")
        )

        val reminders = event.reminderMinutes?.let { mins ->
            EventReminders(
                useDefault = false,
                overrides = listOf(
                    EventReminderOverride(method = "popup", minutes = mins)
                )
            )
        }

        return com.meadow.core.google.api.calendar.model.CalendarEvent(
            id = event.googleEventId,
            summary = event.title,
            description = event.description,
            location = event.location,
            start = start,
            end = end,
            colorId = event.colorId,
            recurrence = event.recurrence,
            reminders = reminders,
            extendedProperties = ExtendedProperties(private = props)
        )
    }

    fun fromGoogle(
        googleCalendarId: String,
        g: com.meadow.core.google.api.calendar.model.CalendarEvent
    ): CalendarEvent? {
        val googleId = g.id ?: return null
        val title = g.summary ?: ""
        val tz = (g.start?.timeZone ?: g.end?.timeZone ?: ZoneId.systemDefault().id)

        val startMillis = parseDateTimeMillis(g.start, tz) ?: return null
        val endMillis = parseDateTimeMillis(g.end, tz) ?: return null

        val privateProps = g.extendedProperties?.private ?: emptyMap()
        val localId = privateProps[GoogleCalendarKeys.KEY_LOCAL_ID]
            ?.takeIf { it.isNotBlank() } ?: googleId

        val scope = privateProps[GoogleCalendarKeys.KEY_SCOPE]
            ?.let { runCatching { CalendarScope.valueOf(it) }.getOrNull() }
            ?: CalendarScope.GLOBAL

        val projectId = privateProps[GoogleCalendarKeys.KEY_PROJECT_ID]
            ?.takeIf { it.isNotBlank() }

        val seriesId = privateProps[GoogleCalendarKeys.KEY_SERIES_ID]
            ?.takeIf { it.isNotBlank() }

        val remoteUpdatedAt = g.updated?.let { parseUpdatedMillis(it) }

        val reminderMinutes = g.reminders?.overrides
            ?.firstOrNull { it.method == "popup" }
            ?.minutes

        return CalendarEvent(
            localId = localId,
            googleEventId = googleId,
            googleCalendarId = googleCalendarId,
            title = title,
            description = g.description,
            location = g.location,
            startAtMillis = startMillis,
            endAtMillis = endMillis,
            allDay = g.start?.date != null,
            timeZone = tz,
            colorId = g.colorId,
            recurrence = g.recurrence,
            reminderMinutes = reminderMinutes,
            scope = scope,
            projectId = projectId,
            seriesId = seriesId,
            isDeleted = g.status == "cancelled",
            localUpdatedAt = remoteUpdatedAt ?: System.currentTimeMillis(),
            remoteUpdatedAt = remoteUpdatedAt,
            etag = g.etag,
            isDirty = false,
            hasConflict = false,
            lastSyncError = null
        )
    }

    private fun parseUpdatedMillis(updated: String): Long? =
        runCatching {
            OffsetDateTime.parse(updated).toInstant().toEpochMilli()
        }.getOrNull()

    private fun parseDateTimeMillis(dt: EventDateTime?, fallbackTz: String): Long? {
        if (dt == null) return null
        val tz = ZoneId.of(dt.timeZone ?: fallbackTz)

        dt.dateTime?.let { s ->
            return runCatching {
                OffsetDateTime.parse(s).toInstant().toEpochMilli()
            }.getOrNull()
        }

        dt.date?.let { d ->
            val localDate = runCatching { LocalDate.parse(d) }.getOrNull() ?: return null
            return localDate.atStartOfDay(tz).toInstant().toEpochMilli()
        }

        return null
    }
}