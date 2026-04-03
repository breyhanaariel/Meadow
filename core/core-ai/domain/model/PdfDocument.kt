package com.meadow.core.ai.domain.model

data class PdfDocument(
    val path: String,
    val title: String,
    val pageCount: Int,
    val projectId: String? = null

)