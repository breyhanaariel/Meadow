package com.meadow.app.utils.system

import android.content.Context
import android.content.res.Configuration
import java.util.*

/**
 * LocalizationUtils.kt
 *
 * Changes app language dynamically at runtime.
 */
object LocalizationUtils {

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
