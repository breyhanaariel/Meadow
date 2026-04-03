package com.meadow.core.data.fields

data class FieldValue(
    val fieldId: String,
    val ownerItemId: String,
    val rawValue: String?,
    val extra: Map<String, String> = emptyMap(),
    val parentFieldId: String? = null
) {

    fun asBoolean(): Boolean? =
        rawValue?.toBooleanStrictOrNull()

    fun asInt(): Int? =
        rawValue?.toIntOrNull()

    fun asDouble(): Double? =
        rawValue?.toDoubleOrNull()

    fun asTags(): List<String> =
        rawValue?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

    fun asList(): List<String> =
        rawValue?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
}
