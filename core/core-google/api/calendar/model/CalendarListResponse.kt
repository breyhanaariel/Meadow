package com.meadow.core.google.api.calendar.model

data class CalendarListResponse(
    val items: List<CalendarListEntry> = emptyList()
)
