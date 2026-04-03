package com.meadow.core.ai.domain.model

data class Bookmark(
    val id: Long? = null,
    val documentPath: String,
    val pageNumber: Int,
    val note: String? = null,
    val linkedCatalogId: String? = null,
    val projectId: String? = null
)
