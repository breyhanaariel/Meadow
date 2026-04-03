package com.meadow.core.data.fields

data class FieldWithValue(
    val definition: FieldDefinition,
    val value: FieldValue?,
    val allValues: List<FieldValue> = emptyList()
) {
    fun effectiveRaw(): String? =
        value?.rawValue ?: definition.defaultValue
}

fun FieldWithValue.childValue(childFieldId: String): FieldValue? =
    allValues.firstOrNull {
        it.fieldId == childFieldId &&
                it.parentFieldId == definition.id
    }

fun FieldWithValue.updated(raw: String?) = FieldValue(definition.id, value?.ownerItemId ?: "", raw)

fun FieldWithValue.isVisible(): Boolean {
    val spec = definition.visibleWhen() ?: return true
    val controllingValue = allValues.firstOrNull { it.fieldId.endsWith(".${spec.field}") }?.rawValue
    spec.equals?.let { expected ->
        return if (expected is Boolean)
            controllingValue?.toBooleanStrictOrNull() == expected
        else
            controllingValue == expected.toString()
    }
    spec.inValues?.let { allowed -> return controllingValue in allowed }
    return true
}
