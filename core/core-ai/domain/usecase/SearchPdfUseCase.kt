package com.meadow.core.ai.domain.usecase

import com.meadow.core.ai.domain.contracts.PdfRepositoryContract
import com.meadow.core.ai.domain.model.PdfDocument
import com.meadow.core.ai.domain.model.PdfSearchResult
import javax.inject.Inject

class SearchPdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepositoryContract
) {
    suspend operator fun invoke(
        document: PdfDocument,
        query: String
    ): List<PdfSearchResult> =
        pdfRepository.searchInDocument(document, query)
}