package com.meadow.core.google.api.calendar

import com.meadow.core.google.api.calendar.model.*
import retrofit2.Response
import retrofit2.http.*

interface CalendarApi {

    // ----------------------------
    // Calendars (Option A setup)
    // ----------------------------

    @GET("calendar/v3/users/me/calendarList")
    suspend fun listCalendars(
        @Query("minAccessRole") minAccessRole: String = "owner"
    ): Response<CalendarListResponse>

    @POST("calendar/v3/calendars")
    suspend fun createCalendar(
        @Body request: CalendarCreateRequest
    ): Response<GoogleCalendar>

    // ----------------------------
    // Events
    // ----------------------------

    @GET("calendar/v3/calendars/{calendarId}/events")
    suspend fun listEvents(
        @Path("calendarId") calendarId: String = "primary",
        @Query("timeMin") timeMin: String? = null,
        @Query("timeMax") timeMax: String? = null,
        @Query("singleEvents") singleEvents: Boolean? = true,
        @Query("orderBy") orderBy: String? = "startTime",
        @Query("showDeleted") showDeleted: Boolean? = null,
        @Query("syncToken") syncToken: String? = null,
        @Query("pageToken") pageToken: String? = null,
        @Query("maxResults") maxResults: Int? = 250
    ): Response<CalendarEventsResponse>

    @POST("calendar/v3/calendars/{calendarId}/events")
    suspend fun insertEvent(
        @Path("calendarId") calendarId: String = "primary",
        @Body event: CalendarEvent
    ): Response<CalendarEvent>

    @PATCH("calendar/v3/calendars/{calendarId}/events/{eventId}")
    suspend fun patchEvent(
        @Path("calendarId") calendarId: String = "primary",
        @Path("eventId") eventId: String,
        @Body event: CalendarEvent
    ): Response<CalendarEvent>

    @DELETE("calendar/v3/calendars/{calendarId}/events/{eventId}")
    suspend fun deleteEvent(
        @Path("calendarId") calendarId: String = "primary",
        @Path("eventId") eventId: String
    ): Response<Unit>
}
