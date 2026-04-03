package com.meadow.core.ai.ui.settings

import androidx.compose.runtime.Composable
import com.meadow.core.common.settings.*

/* ─── AI SETTINGS INJECTOR ───────────────────── */
/* Registers AI settings into the global settings registry */
object AiSettingsInjector : SettingsInjector {

    /* ─── SETTINGS METADATA ───────────────────── */
    override val id = "core_ai_settings"
    override val category = SettingsCategory.AI

    /* ─── REGISTRATION ───────────────────── */
    init {
        SettingsRegistry.register(this)
    }

    /* ─── SETTINGS CONTENT ───────────────────── */
    @Composable
    override fun Content() {
        AiSettingsScreen()
    }
}
