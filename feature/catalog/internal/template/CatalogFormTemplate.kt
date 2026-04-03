package com.meadow.feature.catalog.internal.template


data class CatalogFormTemplate(
    val schemaId: String,
    val sectionOrder: List<String> = emptyList(),
    val collapsibleByDefault: Boolean = true
)
