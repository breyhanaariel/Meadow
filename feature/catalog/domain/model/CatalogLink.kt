package com.meadow.feature.catalog.domain.model

data class CatalogLink(
    val id: String,
    val fromItemId: String,
    val toItemId: String,
    val linkType: String
)
