package com.meadow.feature.project.internal.template

data class ProjectTemplateJson(
    val typeKey: String,
    val fields: List<FieldJson>
)

data class FieldJson(
    val id: String,
    val key: String,
    val labelKey: String,
    val group: String? = null,
    val kind: String,
    val required: Boolean = false,
    val order: Int = 0,
    val metadata: Map<String, Any> = emptyMap(),
    val descriptionKey: String? = null
)
