package com.meadow.core.ui.settings

import androidx.compose.runtime.Composable
import com.meadow.core.common.settings.SettingsCategory
import com.meadow.core.common.settings.SettingsInjector
import com.meadow.core.common.settings.SettingsRegistry
import com.meadow.core.ui.R

object ThemeSettingsInjector : SettingsInjector {

    override val id = "theme_settings"
    override val category = SettingsCategory.THEME
    override val titleRes = R.string.theme_settings_title

    init {
        SettingsRegistry.register(this)
    }

    @Composable
    override fun Content() {
        ThemeSettingsScreen()
    }
}
