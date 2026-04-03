package com.meadow.core.google.engine

data class GoogleConnectionState(
    val signedIn: Boolean,
    val hasDrive: Boolean,
    val hasSheets: Boolean,
    val hasCalendar: Boolean,
    val hasDocs: Boolean
)
