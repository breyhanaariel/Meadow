package com.meadow.core.data.fields

data class FieldHelperSpec(
    val key: String,
    val type: FieldHelperType
)

enum class FieldHelperType {
    BOTTOM_SHEET,
    MODAL
}

internal fun parseHelperType(value: Any?): FieldHelperType =
    runCatching {
        FieldHelperType.valueOf(value as String)
    }.getOrDefault(FieldHelperType.BOTTOM_SHEET)

object FieldHelperLinks {
    private val links = mapOf(
        "helper_character_power_description" to
                "https://powerlisting.fandom.com/wiki/Superpower_Wiki"
    )

    fun getUrl(key: String) = links[key]
}