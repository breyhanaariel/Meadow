package com.meadow.core.ai.domain.model

data class PdfState(
    val documentPath: String,
    val query: String = "",
    val searchResults: List<PdfSearchResult> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    val showLinkDialog: Boolean = false,
    val catalogId: String = "",
    val currentDocument: PdfDocument? = null,
    val currentPage: Int = 0,
    val projectId: String? = null
)
