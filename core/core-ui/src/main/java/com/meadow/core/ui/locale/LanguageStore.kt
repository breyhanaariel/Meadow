package com.meadow.core.ui.locale

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("meadow_language")

class LanguageStore(
    private val context: Context
) {
    private val KEY_LANGUAGE = stringPreferencesKey("language")

    val language: Flow<AppLanguage> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_LANGUAGE]
                ?.let { AppLanguage.valueOf(it) }
                ?: AppLanguage.ENGLISH
        }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language.name
        }
    }
}
