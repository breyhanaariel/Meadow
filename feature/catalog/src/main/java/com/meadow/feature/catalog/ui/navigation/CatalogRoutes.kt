package com.meadow.feature.catalog.ui.navigation

object CatalogRoutes {
    const val GLOBAL_CATALOG = "catalog"
    fun globalCatalog(): String = GLOBAL_CATALOG

    const val PROJECT_CATALOG = "project/{projectId}/catalog"
    fun projectCatalog(projectId: String): String = "project/$projectId/catalog"

    const val SERIES_CATALOG = "series/{seriesId}/catalog"
    fun seriesCatalog(seriesId: String): String = "series/$seriesId/catalog"

    const val CATALOG_ITEM = "catalog/item/{itemId}"
    fun catalogItem(itemId: String): String = "catalog/item/$itemId"

    const val CATALOG_ITEM_CREATE = "catalog/item/create"
    fun catalogCreateItem(
        projectId: String,
        seriesId: String? = null
    ): String =
        buildString {
            append("catalog/item/create?projectId=$projectId")
            if (seriesId != null) append("&seriesId=$seriesId")
        }

    const val CATALOG_ITEM_EDIT = "catalog/item/{itemId}/edit"
    fun catalogEditItem(itemId: String): String = "catalog/item/$itemId/edit"

    const val ArgProjectId = "projectId"
    const val ArgSeriesId = "seriesId"
    const val ArgItemId = "itemId"
}
