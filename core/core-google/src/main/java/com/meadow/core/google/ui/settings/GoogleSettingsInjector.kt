package com.meadow.core.google.ui.settings

import androidx.compose.runtime.Composable
import com.meadow.core.common.settings.*

/* ─── GOOGLE SETTINGS INJECTOR ───────────────────── */
/* Registers Google account & sync settings */
object GoogleSettingsInjector : SettingsInjector {

    /* ─── SETTINGS METADATA ───────────────────── */
    override val id = "core_google_settings"
    override val category = SettingsCategory.GOOGLE

    /* ─── REGISTRATION ───────────────────── */
    init {
        SettingsRegistry.register(this)
    }

    /* ─── SETTINGS CONTENT ───────────────────── */
    @Composable
    override fun Content() {
        GoogleSettingsScreen()
    }
}
