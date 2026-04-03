package com.meadow.feature.project.domain.model

data class Series(
    val id: String,
    val title: String,
    val projectIds: List<String>,
    val sharedFieldIds: List<String>,
    val sharedCatalogFieldIds: Set<String>
)
