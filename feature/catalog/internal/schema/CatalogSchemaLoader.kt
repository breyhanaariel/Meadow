package com.meadow.feature.catalog.internal.schema

import android.content.Context
import com.meadow.core.data.fields.FieldDefinition
import com.meadow.core.data.fields.FieldKind
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class CatalogSchemaLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun loadJson(assetPath: String): JSONObject =
        context.assets.open(assetPath)
            .bufferedReader()
            .use { JSONObject(it.readText()) }

    fun loadFields(assetPath: String): List<FieldDefinition> {
        val root = loadJson(assetPath)

        val owner = root.optString("typeKey")
            .takeIf { it.isNotBlank() }
            ?: root.optString("schemaId")
                .takeIf { it.isNotBlank() }
            ?: "catalog"

        val fieldsArr = root.optJSONArray("fields") ?: JSONArray()

        return (0 until fieldsArr.length())
            .mapNotNull { idx -> fieldsArr.optJSONObject(idx) }
            .map { fieldJson -> parseFieldDefinition(owner = owner, obj = fieldJson) }
            .sortedBy { it.order }
    }

    private fun parseFieldDefinition(owner: String, obj: JSONObject): FieldDefinition {
        val id = obj.optString("id")
        val key = obj.optString("key")
        val labelKey = obj.optString("labelKey")
        val hintKey = obj.optString("hintKey").takeIf { it.isNotBlank() }
        val descriptionKey = obj.optString("descriptionKey").takeIf { it.isNotBlank() }
        val group = obj.optString("group").takeIf { it.isNotBlank() }
        val order = obj.optInt("order", 0)
        val required = obj.optBoolean("required", false)
        val readOnly = obj.optBoolean("readOnly", false)
        val defaultValue = obj.optString("defaultValue").takeIf { it.isNotBlank() }

        val kindStr = obj.optString("kind").trim()

        val kind = try {
            FieldKind.valueOf(kindStr)
        } catch (e: IllegalArgumentException) {
            error(
                "Unknown FieldKind '$kindStr' " +
                        "in schema '$owner', field '${obj.optString("id")}'"
            )
        }

        val metadataObj = obj.optJSONObject("metadata")
        val metadata: Map<String, Any> = metadataObj?.let { toMap(it) } ?: emptyMap()

        return FieldDefinition(
            id = id,
            owner = owner,
            key = key,
            labelKey = labelKey,
            hintKey = hintKey,
            descriptionKey = descriptionKey,
            kind = kind,
            group = group,
            order = order,
            isRequired = required,
            isReadOnly = readOnly,
            defaultValue = defaultValue,
            metadata = metadata
        )
    }

    private fun toMap(obj: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            val v = obj.get(k)
            map[k] = when (v) {
                JSONObject.NULL -> ""
                is JSONObject -> toMap(v)
                is JSONArray -> v.toString()
                else -> v
            }
        }
        return map
    }
}