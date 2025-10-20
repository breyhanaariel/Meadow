package com.meadow.app.util

import com.meadow.app.data.room.entities.CatalogItemEntity

fun embedLinks(text: String, catalog: List<CatalogItemEntity>): Pair<String, List<String>> {
    var out = text
    val linkedIds = mutableListOf<String>()
    catalog.forEach { item ->
        val marker = "@${item.title}"
        if (out.contains(marker)) {
            // Keep display as plain name; store ID separately in metadata
            out = out.replace(marker, item.title)
            linkedIds.add(item.id)
        }
    }
    return Pair(out, linkedIds)
}
