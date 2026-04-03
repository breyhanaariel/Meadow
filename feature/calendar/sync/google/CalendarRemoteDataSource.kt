package com.meadow.feature.calendar.sync.google

import com.meadow.core.google.api.calendar.CalendarApi
import com.meadow.core.google.api.calendar.model.CalendarCreateRequest
import com.meadow.core.google.api.calendar.model.CalendarEvent
import com.meadow.core.google.api.calendar.model.CalendarEventsResponse
import com.meadow.core.google.api.calendar.model.CalendarListResponse
import com.meadow.core.google.api.calendar.model.GoogleCalendar
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRemoteDataSource @Inject constructor(
    private val api: CalendarApi
) {

    suspend fun listCalendars(): Response<CalendarListResponse> {
        return api.listCalendars(minAccessRole = "owner")
    }

    suspend fun createCalendar(summary: String): Response<GoogleCalendar> {
        return api.createCalendar(CalendarCreateRequest(summary = summary))
    }

    suspend fun listEvents(
        calendarId: String,
        timeMin: String?,
        timeMax: String?,
        syncToken: String?,
        showDeleted: Boolean?,
        pageToken: String?
    ): Response<CalendarEventsResponse> {
        return api.listEvents(
            calendarId = calendarId,
            timeMin = timeMin,
            timeMax = timeMax,
            singleEvents = true,
            orderBy = "startTime",
            showDeleted = showDeleted,
            syncToken = syncToken,
            pageToken = pageToken,
            maxResults = 250
        )
    }

    suspend fun insertEvent(
        calendarId: String,
        event: CalendarEvent
    ): Response<CalendarEvent> {
        return api.insertEvent(calendarId = calendarId, event = event)
    }

    suspend fun patchEvent(
        calendarId: String,
        eventId: String,
        event: CalendarEvent
    ): Response<CalendarEvent> {
        return api.patchEvent(calendarId = calendarId, eventId = eventId, event = event)
    }

    suspend fun deleteEvent(
        calendarId: String,
        eventId: String
    ): Response<Unit> {
        return api.deleteEvent(calendarId = calendarId, eventId = eventId)
    }
}
