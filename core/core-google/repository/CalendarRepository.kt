package com.meadow.core.google.repository

import com.meadow.core.google.api.calendar.CalendarApi
import com.meadow.core.google.api.calendar.model.*
import retrofit2.Response
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val api: CalendarApi
) {

    suspend fun listCalendars(): Response<CalendarListResponse> =
        api.listCalendars()

    suspend fun createCalendar(summary: String): Response<GoogleCalendar> =
        api.createCalendar(CalendarCreateRequest(summary = summary))

    suspend fun listEvents(
        calendarId: String = "primary",
        timeMin: String? = null,
        timeMax: String? = null,
        syncToken: String? = null,
        showDeleted: Boolean? = null
    ): Response<CalendarEventsResponse> =
        api.listEvents(
            calendarId = calendarId,
            timeMin = timeMin,
            timeMax = timeMax,
            syncToken = syncToken,
            showDeleted = showDeleted
        )

    suspend fun insertEvent(
        calendarId: String = "primary",
        event: CalendarEvent
    ): Response<CalendarEvent> =
        api.insertEvent(calendarId = calendarId, event = event)

    suspend fun patchEvent(
        calendarId: String = "primary",
        eventId: String,
        event: CalendarEvent
    ): Response<CalendarEvent> =
        api.patchEvent(calendarId = calendarId, eventId = eventId, event = event)

    suspend fun deleteEvent(
        calendarId: String = "primary",
        eventId: String
    ): Response<Unit> =
        api.deleteEvent(calendarId = calendarId, eventId = eventId)
}
