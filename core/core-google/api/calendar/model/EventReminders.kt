package com.meadow.core.google.api.calendar.model

data class EventReminders(
    val useDefault: Boolean? = null,
    val overrides: List<EventReminderOverride>? = null
)
