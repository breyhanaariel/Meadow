package com.meadow.core.google.api.calendar.model

data class CalendarEventsResponse(
    val items: List<CalendarEvent>? = emptyList(),
    val nextPageToken: String? = null,
    val nextSyncToken: String? = null
)
