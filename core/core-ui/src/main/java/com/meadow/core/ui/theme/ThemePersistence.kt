package com.meadow.core.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("meadow_theme")

object ThemePersistence {

    private val KEY_THEME = stringPreferencesKey("theme")
    private val KEY_SOFT_GLOW = booleanPreferencesKey("soft_glow")
    private val KEY_GLITTER = booleanPreferencesKey("glitter")
    private val KEY_PETALS = booleanPreferencesKey("petals")
    private val KEY_PARTICLES = booleanPreferencesKey("particles")

    private val KEY_FIELD_CORNER = intPreferencesKey("field_corner_style")
    private val KEY_FIELD_FILL = intPreferencesKey("field_fill_style")

    fun themeFlow(context: Context): Flow<MeadowThemeVariant> =
        context.dataStore.data.map { prefs ->
            val name = prefs[KEY_THEME] ?: MeadowThemeVariant.Lavender.name
            MeadowThemeVariant.valueOf(name)
        }

    suspend fun saveTheme(context: Context, theme: MeadowThemeVariant) {
        context.dataStore.edit { it[KEY_THEME] = theme.name }
    }

    fun effectFlow(context: Context): Flow<EffectSettings> =
        context.dataStore.data.map { prefs ->
            EffectSettings(
                enableSoftGlow = prefs[KEY_SOFT_GLOW] ?: false,
                enableGlitter = prefs[KEY_GLITTER] ?: false,
                enablePetals = prefs[KEY_PETALS] ?: false,
                enableFloatingParticles = prefs[KEY_PARTICLES] ?: false,
                fieldCornerStyle = prefs[KEY_FIELD_CORNER] ?: 1,
                fieldFillStyle = prefs[KEY_FIELD_FILL] ?: 1
            )
        }

    suspend fun saveEffect(context: Context, settings: EffectSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SOFT_GLOW] = settings.enableSoftGlow
            prefs[KEY_GLITTER] = settings.enableGlitter
            prefs[KEY_PETALS] = settings.enablePetals
            prefs[KEY_PARTICLES] = settings.enableFloatingParticles
            prefs[KEY_FIELD_CORNER] = settings.fieldCornerStyle
            prefs[KEY_FIELD_FILL] = settings.fieldFillStyle
        }
    }
}
