package com.meadow.feature.calendar.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.calendarDataStore by preferencesDataStore(name = "calendar_prefs")

@Singleton
class CalendarPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val MeadowCalendarId = stringPreferencesKey("meadow_calendar_id")
        val MeadowSyncToken = stringPreferencesKey("meadow_sync_token")
    }

    val meadowCalendarIdFlow: Flow<String?> =
        context.calendarDataStore.data.map { it[Keys.MeadowCalendarId] }

    val syncTokenFlow: Flow<String?> =
        context.calendarDataStore.data.map { it[Keys.MeadowSyncToken] }

    suspend fun getMeadowCalendarId(): String? =
        context.calendarDataStore.data.first()[Keys.MeadowCalendarId]

    suspend fun setMeadowCalendarId(id: String) {
        context.calendarDataStore.edit { prefs ->
            prefs[Keys.MeadowCalendarId] = id
        }
    }

    suspend fun clearMeadowCalendarId() {
        context.calendarDataStore.edit { prefs ->
            prefs.remove(Keys.MeadowCalendarId)
        }
    }

    suspend fun getSyncToken(): String? =
        context.calendarDataStore.data.first()[Keys.MeadowSyncToken]

    suspend fun setSyncToken(token: String?) {
        context.calendarDataStore.edit { prefs ->
            if (token == null) prefs.remove(Keys.MeadowSyncToken)
            else prefs[Keys.MeadowSyncToken] = token
        }
    }
}
