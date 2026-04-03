package com.meadow.feature.catalog.sync.sheets

object CatalogSheetsSheetResolver {

    fun resolveSheetName(
        projectId: String?,
        seriesId: String?
    ): String =
        when {
            projectId != null -> "project_$projectId"
            seriesId != null -> "series_$seriesId"
            else -> error("CatalogItem must have projectId or seriesId")
        }
}
