package com.meadow.feature.catalog.domain.registry


object CatalogVisibilityRules {

    fun isSchemaVisibleForProjectType(
        schemaId: String,
        projectTypeKey: String?
    ): Boolean {
        return true
    }
}
