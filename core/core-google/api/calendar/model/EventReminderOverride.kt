package com.meadow.core.google.api.calendar.model

data class EventReminderOverride(
    val method: String? = null, // "email" | "popup"
    val minutes: Int? = null
)
