package com.meadow.core.ai.utils

import com.meadow.core.ai.domain.model.PdfDocument
import com.meadow.core.ai.domain.model.PdfRect
import com.meadow.core.ai.domain.model.PdfSearchResult

object PdfUtils {

    fun searchText(
        document: PdfDocument,
        pageTexts: List<String>,
        query: String
    ): List<PdfSearchResult> {
        if (query.isBlank()) return emptyList()
        val lower = query.lowercase()
        return pageTexts.mapIndexedNotNull { index, text ->
            val pos = text.lowercase().indexOf(lower)
            if (pos >= 0) {
                val start = (pos - 40).coerceAtLeast(0)
                val end = (pos + query.length + 40).coerceAtMost(text.length)
                val snippet = text.substring(start, end).trim()
                PdfSearchResult(
                    documentPath = document.path,
                    documentTitle = document.title,
                    pageNumber = index + 1,
                    pageIndex = index,
                    snippet = snippet,
                    rect = PdfRect(
                        left = 0f,
                        top = 0f,
                        width = 0f,
                        height = 0f
                    )
                )
            } else {
                null
            }
        }
    }
}