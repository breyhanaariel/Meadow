package com.meadow.core.ai.pdf.reference

import com.meadow.core.ai.domain.model.PdfDocument
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class ReferenceIndexer @Inject constructor(
    private val store: ReferenceStore
) {

    suspend fun ensureIndexed(pdfDocuments: List<PdfDocument>) {
        val existing = store.getAllDocuments().associateBy { it.path }

        for (pdf in pdfDocuments) {
            val lastModified = java.io.File(pdf.path).lastModified()
            val already = existing[pdf.path]

            val needsIndex =
                already == null || already.lastModified != lastModified

            if (needsIndex) {
                indexOne(pdf, lastModified)
            }
        }
    }

    private suspend fun indexOne(pdf: PdfDocument, lastModified: Long) {
        val docId = stableDocId(pdf.path)
        val pages = PdfTextExtractor.extractPages(pdf.path)

        val docEntity = ReferenceDocumentEntity(
            id = docId,
            title = pdf.title,
            path = pdf.path,
            pageCount = pages.size,
            lastModified = lastModified,
            indexedAt = System.currentTimeMillis()
        )

        store.upsertDocument(docEntity)

        val chunks = buildChunks(
            documentId = docId,
            documentTitle = pdf.title,
            documentPath = pdf.path,
            pages = pages
        )

        store.replaceDocumentChunks(docId, chunks)
    }

    private fun buildChunks(
        documentId: String,
        documentTitle: String,
        documentPath: String,
        pages: List<PdfPageText>
    ): List<ReferenceChunk> {

        val out = mutableListOf<ReferenceChunk>()
        val maxChars = 1200
        val overlap = 200

        for (p in pages) {
            val text = p.text.trim()
            if (text.isBlank()) continue

            if (text.length <= maxChars) {
                out += ReferenceChunk(
                    id = UUID.randomUUID().toString(),
                    documentId = documentId,
                    documentTitle = documentTitle,
                    documentPath = documentPath,
                    pageNumber = p.pageNumber,
                    text = text
                )
            } else {
                var start = 0
                while (start < text.length) {
                    val end = minOf(text.length, start + maxChars)
                    val slice = text.substring(start, end).trim()

                    if (slice.isNotBlank()) {
                        out += ReferenceChunk(
                            id = UUID.randomUUID().toString(),
                            documentId = documentId,
                            documentTitle = documentTitle,
                            documentPath = documentPath,
                            pageNumber = p.pageNumber,
                            text = slice
                        )
                    }

                    if (end == text.length) break
                    start = (end - overlap).coerceAtLeast(0)
                }
            }
        }

        return out
    }

    private fun stableDocId(path: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(path.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(32)
    }
}
