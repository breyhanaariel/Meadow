package com.meadow.core.common.settings

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences

enum class SettingsCategory {
    FEATURE,
    THEME,
    AI,
    GOOGLE,
    OTHER
}

/* ───  INJECT SETTINGS ON APP SETTINGS SCREEN ───────────────────── */

interface SettingsInjector {
    val id: String
    val category: SettingsCategory
    val titleRes: Int?
        get() = null
    val featureKey: Preferences.Key<Boolean>?
        get() = null

    @Composable
    fun Content()
}

object SettingsRegistry {

    private val injectors = mutableListOf<SettingsInjector>()

    fun register(injector: SettingsInjector) {
        if (injectors.none { it.id == injector.id }) {
            injectors.add(injector)
        }
    }

    fun getAll(): List<SettingsInjector> = injectors.toList()

    fun forCategory(category: SettingsCategory): List<SettingsInjector> =
        injectors.filter { it.category == category }

    fun forFeature(key: Preferences.Key<Boolean>): List<SettingsInjector> =
        injectors.filter { it.featureKey == key }

    fun ensureAllInjectorsLoaded() {
        val injectorClasses = listOf(
            "com.meadow.core.ui.settings.ThemeSettingsInjector",
            "com.meadow.core.ai.ui.settings.AiSettingsInjector",
            "com.meadow.core.google.ui.settings.GoogleSettingsInjector"
        )

        injectorClasses.forEach { className ->
            try {
                Class.forName(className)
            } catch (_: Throwable) {
            }
        }
    }
}
