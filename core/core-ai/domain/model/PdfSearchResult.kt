package com.meadow.core.ai.domain.model

data class PdfSearchResult(
    val documentPath: String,
    val documentTitle: String,
    val pageNumber: Int,
    val pageIndex: Int,
    val rect: PdfRect,
    val snippet: String
)