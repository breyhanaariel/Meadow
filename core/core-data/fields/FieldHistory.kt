package com.meadow.core.data.fields

data class FieldChange(
    val fieldId: String,
    val oldValue: String?,
    val newValue: String?,
    val parentFieldId: String? = null
)

object FieldDiffEngine {

    fun diffFieldValues(
        oldValues: List<FieldValue>,
        newValues: List<FieldValue>
    ): List<FieldChange> {
        val oldMap = oldValues.associateBy { FieldPath(it.fieldId, it.parentFieldId) }
        val newMap = newValues.associateBy { FieldPath(it.fieldId, it.parentFieldId) }
        val allKeys = (oldMap.keys + newMap.keys).distinct()

        return allKeys.mapNotNull { key ->
            val oldValue = oldMap[key]?.rawValue.normalizeForHistory()
            val newValue = newMap[key]?.rawValue.normalizeForHistory()

            if (oldValue == newValue) {
                null
            } else {
                FieldChange(
                    fieldId = key.fieldId,
                    oldValue = oldValue,
                    newValue = newValue,
                    parentFieldId = key.parentFieldId
                )
            }
        }
    }

    fun diffMaps(
        oldValues: Map<String, String?>,
        newValues: Map<String, String?>
    ): List<FieldChange> {
        val allKeys = (oldValues.keys + newValues.keys).distinct()

        return allKeys.mapNotNull { key ->
            val oldValue = oldValues[key].normalizeForHistory()
            val newValue = newValues[key].normalizeForHistory()

            if (oldValue == newValue) {
                null
            } else {
                FieldChange(
                    fieldId = key,
                    oldValue = oldValue,
                    newValue = newValue
                )
            }
        }
    }

    private fun String?.normalizeForHistory(): String? =
        this
            ?.trim()
            ?.ifBlank { null }

    private data class FieldPath(
        val fieldId: String,
        val parentFieldId: String?
    )
}