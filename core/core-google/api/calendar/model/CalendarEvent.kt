package com.meadow.core.google.api.calendar.model

data class CalendarEvent(
    val id: String? = null,
    val etag: String? = null,
    val status: String? = null, // "confirmed" | "cancelled"
    val updated: String? = null, // RFC3339
    val summary: String? = null,
    val description: String? = null,
    val location: String? = null,
    val start: EventDateTime? = null,
    val end: EventDateTime? = null,
    val colorId: String? = null,
    val recurrence: List<String>? = null,
    val reminders: EventReminders? = null,
    val extendedProperties: ExtendedProperties? = null
)
