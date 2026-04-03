package com.meadow.core.data.fields

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

data class FieldDefinition(
    val id: String,
    val owner: String,
    val key: String,
    val labelKey: String,
    val hintKey: String? = null,
    val descriptionKey: String? = null,
    val kind: FieldKind,
    val group: String? = null,
    val order: Int = 0,
    val isRequired: Boolean = false,
    val isReadOnly: Boolean = false,
    val defaultValue: String? = null,
    val children: List<FieldDefinition> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
)
object FieldHelpMetaKeys {
    const val TOOLTIP_KEY = "tooltipKey"
    const val HELPER_KEY = "helperKey"
    const val HELPER_TYPE = "helperType"
    const val AI_HELPER = "aiHelper"
}

fun FieldDefinition.tooltipKey(): String? =
    metadata[FieldHelpMetaKeys.TOOLTIP_KEY] as? String

fun FieldDefinition.helperSpec(): FieldHelperSpec? {
    val key = metadata[FieldHelpMetaKeys.HELPER_KEY] as? String ?: return null
    val typeRaw = metadata[FieldHelpMetaKeys.HELPER_TYPE] as? String
    val type = parseHelperType(typeRaw)

    return FieldHelperSpec(
        key = key,
        type = type
    )
}

fun FieldDefinition.isAiHelper(): Boolean =
    metadata[FieldHelpMetaKeys.AI_HELPER] as? Boolean == true

fun FieldDefinition.options() = (metadata["options"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

fun FieldDefinition.children(): List<FieldDefinition> = children

data class FieldHelp(
    val tooltipKey: String?,
    val helper: FieldHelperSpec?
)

fun FieldDefinition.help(): FieldHelp =
    FieldHelp(
        tooltipKey = tooltipKey(),
        helper = helperSpec()
    )

data class VisibleWhenSpec(
    val field: String,
    val equals: Any? = null,
    val inValues: List<String>? = null
)

fun FieldDefinition.visibleWhen(): VisibleWhenSpec? {
    val raw = metadata["visibleWhen"] as? Map<*, *> ?: return null

    val field = raw["field"] as? String ?: return null
    val equals = raw["equals"]
    val inValues = raw["in"] as? List<String>

    return VisibleWhenSpec(
        field = field,
        equals = equals,
        inValues = inValues
    )
}
