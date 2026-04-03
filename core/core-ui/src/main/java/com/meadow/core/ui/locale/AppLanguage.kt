package com.meadow.core.ui.locale

import java.util.Locale

enum class AppLanguage(
    val tag: String,
    val label: String
) {
    ENGLISH("en", "English"),
    CHINESE_SIMPLIFIED("zh-CN", "中文 (简体)"),
    SPANISH("es", "Español"),
    HINDI("hi", "हिन्दी"),
    PORTUGUESE_BRAZIL("pt-BR", "Português (Brasil)"),
    RUSSIAN("ru", "Русский"),
    JAPANESE("ja", "日本語"),
    GERMAN("de", "Deutsch"),
    FRENCH("fr", "Français"),
    INDONESIAN("id", "Bahasa Indonesia"),
    VIETNAMESE("vi", "Tiếng Việt"),
    TURKISH("tr", "Türkçe"),
    KOREAN("ko", "한국어"),
    ITALIAN("it", "Italiano"),
    POLISH("pl", "Polski"),
    UKRAINIAN("uk", "Українська"),
    THAI("th", "ไทย"),
    DUTCH("nl", "Nederlands"),
    ROMANIAN("ro", "Română"),
    SWEDISH("sv", "Svenska"),
    CZECH("cs", "Čeština");

    fun toLocale(): Locale = Locale.forLanguageTag(tag)

    companion object {
        fun fromLocale(locale: Locale): AppLanguage =
            values().firstOrNull {
                locale.toLanguageTag().startsWith(it.tag)
            } ?: ENGLISH
    }
}
