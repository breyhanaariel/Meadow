package com.meadow.feature.project.domain.model

import com.meadow.core.data.fields.FieldDefinition

data class ProjectTemplate(
    val typeKey: String,
    val fields: List<FieldDefinition>,
    val enabledFeatures: List<String> = emptyList()
)