package com.meadow.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "meadow_settings")

class SettingsDataStore {
    private val THEME_KEY = stringPreferencesKey("theme")
    private val GEMINI_PROMPTS_KEY = stringPreferencesKey("gemini_prompts")
    private val GOOGLE_KEYS_KEY = stringPreferencesKey("google_api_keys")

    suspend fun saveTheme(context: Context, theme: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme
        }
    }

    fun loadTheme(context: Context) = context.dataStore.data.map {
        it[THEME_KEY] ?: "Lavender Dream"
    }

    suspend fun saveGeminiPrompts(context: Context, promptsJson: String) {
        context.dataStore.edit { prefs ->
            prefs[GEMINI_PROMPTS_KEY] = promptsJson
        }
    }

    fun loadGeminiPrompts(context: Context) = context.dataStore.data.map {
        it[GEMINI_PROMPTS_KEY] ?: "{}"
    }

    suspend fun saveGoogleApiKeys(context: Context, encryptedJson: String) {
        context.dataStore.edit { prefs ->
            prefs[GOOGLE_KEYS_KEY] = encryptedJson
        }
    }

    fun loadGoogleApiKeys(context: Context) = context.dataStore.data.map {
        it[GOOGLE_KEYS_KEY] ?: "{}"
    }
}
