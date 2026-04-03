package com.meadow.core.ai.domain.contracts

import com.meadow.core.ai.domain.model.PdfDocument
import com.meadow.core.ai.domain.model.PdfSearchResult

interface PdfRepositoryContract {
    suspend fun listDocuments(): List<PdfDocument>

    suspend fun searchInDocument(
        document: PdfDocument,
        query: String
    ): List<PdfSearchResult>
}