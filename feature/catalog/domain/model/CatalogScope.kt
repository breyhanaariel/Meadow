package com.meadow.feature.catalog.domain.model

sealed class CatalogScope {
    object Global : CatalogScope()
    data class Project(val projectId: String) : CatalogScope()
    data class Series(val seriesId: String) : CatalogScope()
}