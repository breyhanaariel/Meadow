package com.meadow.app.core.constants

/**
 * AppConstants.kt
 *
 * This file defines global constants used across the app.
 * Keeping constants in one place helps avoid hardcoding values everywhere.
 */

object AppConstants {
    // App-wide identifiers and names
    const val APP_NAME = "Meadow"
    const val DATABASE_NAME = "meadow_db"

    // Common timing intervals (in milliseconds)
    const val AUTO_SAVE_INTERVAL = 10_000L // autosave every 10 seconds
    const val SYNC_DELAY = 5_000L // delay before sync triggers

    // Firebase Collections
    const val COLLECTION_PROJECTS = "projects"
    const val COLLECTION_CATALOG = "catalog_items"

    // DataStore keys (used for settings)
    const val DATASTORE_NAME = "meadow_prefs"

    // Default themes
    val DEFAULT_THEME = "Lavender"
}
