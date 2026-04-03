package com.meadow.core.ai.pdf.reference

import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.File

data class PdfPageText(
    val pageNumber: Int,
    val text: String
)

object PdfTextExtractor {

    fun extractPages(pdfPath: String): List<PdfPageText> {
        val file = File(pdfPath)
        if (!file.exists()) return emptyList()

        return try {
            PDDocument.load(file).use { doc ->
                val pageCount = doc.numberOfPages
                val stripper = PDFTextStripper()

                (1..pageCount).map { page ->
                    stripper.startPage = page
                    stripper.endPage = page
                    val text = stripper.getText(doc).orEmpty().trim()
                    PdfPageText(pageNumber = page, text = text)
                }
            }
        } catch (e: Exception) {
            Log.e("PdfTextExtractor", "Failed extracting text: $pdfPath", e)
            emptyList()
        }
    }
}
