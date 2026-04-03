package com.meadow.core.google.auth

object GoogleScopes {

    const val DRIVE_FILE =
        "https://www.googleapis.com/auth/drive.file"

    const val SHEETS =
        "https://www.googleapis.com/auth/spreadsheets"

    const val DOCS =
        "https://www.googleapis.com/auth/documents"

    /**
     * Events only (read/write). This is sufficient for reading/creating events,
     * but NOT for creating new calendars.
     */
    const val CALENDAR_EVENTS =
        "https://www.googleapis.com/auth/calendar.events"

    /**
     * Full Calendar scope (needed to create a dedicated Meadow calendar).
     */
    const val CALENDAR =
        "https://www.googleapis.com/auth/calendar"

    val BACKUP_SCOPES = listOf(
        DRIVE_FILE
    )

    val ALL_SCOPES = listOf(
        DRIVE_FILE,
        SHEETS,
        DOCS,
        CALENDAR,
        CALENDAR_EVENTS
    )
}
