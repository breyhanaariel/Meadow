package com.meadow.core.ai.pdf

import android.content.Context
import android.util.Log
import com.meadow.core.ai.domain.contracts.PdfRepositoryContract
import com.meadow.core.ai.domain.model.PdfDocument
import com.meadow.core.ai.domain.model.PdfRect
import com.meadow.core.ai.domain.model.PdfSearchResult
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PdfRepository(
    private val context: Context
) : PdfRepositoryContract {

    private val assetFolder = "pdfs"

    private val cacheFolder: File by lazy {
        File(context.cacheDir, "pdfs").apply { mkdirs() }
    }

    override suspend fun listDocuments(): List<PdfDocument> {

        val assets = try {
            context.assets.list(assetFolder) ?: emptyArray()
        } catch (e: Exception) {
            Log.e("PdfRepository", "Error listing assets", e)
            emptyArray()
        }

        return assets
            .filter { it.endsWith(".pdf", ignoreCase = true) }
            .mapNotNull { fileName ->

                val cachedFile = copyAssetIfNeeded(fileName)
                    ?: return@mapNotNull null

                val pageCount = try {
                    PDDocument.load(cachedFile).use { it.numberOfPages }
                } catch (e: Exception) {
                    0
                }

                PdfDocument(
                    path = cachedFile.absolutePath,
                    title = fileName.removeSuffix(".pdf").replace('_', ' '),
                    pageCount = pageCount
                )
            }
    }

    private fun copyAssetIfNeeded(fileName: String): File? {

        val destination = File(cacheFolder, fileName)

        if (destination.exists() && destination.length() > 0) {
            return destination
        }

        return try {
            context.assets.open("$assetFolder/$fileName").use { input ->
                FileOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
            destination
        } catch (e: IOException) {
            Log.e("PdfRepository", "Failed copying $fileName", e)
            null
        }
    }

    override suspend fun searchInDocument(
        document: PdfDocument,
        query: String
    ): List<PdfSearchResult> {

        if (query.isBlank()) return emptyList()

        val results = mutableListOf<PdfSearchResult>()

        val file = File(document.path)

        PDDocument.load(file).use { pdf ->

            val stripper = object : PDFTextStripper() {

                override fun writeString(
                    text: String,
                    textPositions: MutableList<TextPosition>
                ) {

                    val lowerQuery = query.lowercase()
                    val lowerText = text.lowercase()

                    if (!lowerText.contains(lowerQuery)) return

                    textPositions.forEach { position ->

                        if (position.unicode.lowercase().contains(lowerQuery)) {

                            val pageIndex = currentPageNo - 1

                            val pageHeight = pdf.getPage(pageIndex).mediaBox.height

                            val rect = PdfRect(
                                left = position.xDirAdj,
                                top = pageHeight - position.yDirAdj,
                                width = position.widthDirAdj,
                                height = position.heightDir
                            )

                            results.add(
                                PdfSearchResult(
                                    documentPath = document.path,
                                    documentTitle = document.title,
                                    pageNumber = pageIndex + 1,
                                    pageIndex = pageIndex,
                                    rect = rect,
                                    snippet = text.trim()
                                )
                            )
                        }
                    }
                }
            }

            stripper.startPage = 1
            stripper.endPage = pdf.numberOfPages

            stripper.getText(pdf)
        }

        return results
    }
}