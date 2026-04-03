package com.meadow.feature.catalog.sync.drive

import com.meadow.feature.catalog.domain.model.CatalogItem

object CatalogDriveFileNamer {

    fun fileName(catalog: CatalogItem): String {
        val schemaType = catalog.schemaId.ifBlank { "catalog" }

        val title = catalog.fields
            .firstOrNull { it.definition.id == "title" }
            ?.effectiveRaw()
            ?: "Catalog"

        val safeTitle = title
            .replace(Regex("[^a-zA-Z0-9 _-]"), "")
            .trim()

        val safeSchema = schemaType
            .replace(Regex("[^a-zA-Z0-9 _-]"), "")
            .trim()

        return "${safeSchema}_${safeTitle}_${catalog.id}.json"
    }
}
