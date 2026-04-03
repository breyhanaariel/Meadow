package com.meadow.feature.catalog.sync.drive

import com.meadow.feature.catalog.domain.model.CatalogItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogDriveSerializer @Inject constructor() {

    fun serialize(catalog: CatalogItem): String {
        return buildString {
            append("CATALOG_ITEM|")
            append(catalog.id)
        }
    }


    fun deserialize(json: String): List<CatalogItem> {
        return emptyList()
    }
}
