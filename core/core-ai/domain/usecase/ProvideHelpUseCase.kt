package com.meadow.core.ai.domain.usecase

import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.contracts.PdfRepositoryContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.PdfDocument
import com.meadow.core.ai.R
import com.meadow.core.ui.R as CoreUiR

class ProvideHelpUseCase(
    private val aiRepository: AiRepositoryContract,
    private val pdfRepository: PdfRepositoryContract
) {
    suspend operator fun invoke(
        question: String,
        document: PdfDocument,
        query: String
    ) = run {
        val matches = pdfRepository.searchInDocument(document, query)

        val combinedContext = buildString {
            matches.take(8).forEach { result ->
                appendLine("${CoreUiR.string.help_source}: ${result.documentTitle} (p.${result.pageNumber})")
                appendLine(result.snippet)
                appendLine()
            }
        }

        aiRepository.generateResponse(
            persona = AiPersona.Meadow,
            input = question,
            extraContext = combinedContext
        )
    }
}