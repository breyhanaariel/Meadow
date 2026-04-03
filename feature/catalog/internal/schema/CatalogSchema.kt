package com.meadow.feature.catalog.internal.schema

import androidx.annotation.DrawableRes
import com.meadow.core.data.fields.FieldDefinition
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import org.json.JSONArray
import org.json.JSONObject

data class CatalogSchema(
    val schemaId: String,
    val labelKey: String,
    @DrawableRes val iconResId: Int,
    val assetPath: String,
    val primaryFieldKey: String,
    val appliesToProjectTypeKeys: Set<String>,
    val fields: List<FieldDefinition>
) {

    fun titleFromJson(fieldValuesJson: String, fallback: String = "Untitled"): String {
        return runCatching {
            val obj = JSONObject(fieldValuesJson)
            obj.optString(primaryFieldKey).takeIf { it.isNotBlank() }
                ?: obj.optString("title").takeIf { it.isNotBlank() }
                ?: obj.optString("name").takeIf { it.isNotBlank() }
                ?: obj.optString("full_name").takeIf { it.isNotBlank() }
                ?: fallback
        }.getOrElse { fallback }
    }


    fun toFieldsWithValues(
        ownerItemId: String,
        fieldValuesJson: String?
    ): List<FieldWithValue> {
        val obj = runCatching {
            if (fieldValuesJson.isNullOrBlank()) JSONObject() else JSONObject(fieldValuesJson)
        }.getOrElse { JSONObject() }

        return fields
            .sortedBy { it.order }
            .map { def ->
                val raw: String? = if (obj.has(def.key)) {
                    val any = obj.get(def.key)
                    when (any) {
                        JSONObject.NULL -> null
                        is JSONArray -> any.toString()
                        is JSONObject -> any.toString()
                        else -> any.toString()
                    }
                } else null

                val value = FieldValue(
                    fieldId = def.id,
                    ownerItemId = ownerItemId,
                    rawValue = raw
                )

                FieldWithValue(definition = def, value = value)
            }
    }


    fun toFieldValuesJson(fieldsWithValues: List<FieldWithValue>): String {
        val obj = JSONObject()

        fieldsWithValues.forEach { fwv ->
            val def = fwv.definition
            val raw = fwv.value?.rawValue ?: def.defaultValue

            if (raw.isNullOrBlank()) return@forEach

            val trimmed = raw.trim()
            val putAsJson = (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                    (trimmed.startsWith("[") && trimmed.endsWith("]"))

            if (putAsJson) {
                val parsed = runCatching {
                    if (trimmed.startsWith("{")) JSONObject(trimmed) else JSONArray(trimmed)
                }.getOrNull()

                if (parsed != null) {
                    obj.put(def.key, parsed)
                } else {
                    obj.put(def.key, raw)
                }
            } else {
                obj.put(def.key, raw)
            }
        }

        return obj.toString()
    }
}