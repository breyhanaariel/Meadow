package com.meadow.core.ai.pdf.reference

import com.meadow.core.ai.domain.model.PdfSearchResult
import javax.inject.Inject
import kotlin.math.min

class ReferenceRetriever @Inject constructor(
    private val dao: ReferenceDao
) {

    data class Hit(
        val documentTitle: String,
        val documentPath: String,
        val pageNumber: Int,
        val snippet: String,
        val score: Int
    )

    suspend fun retrieve(question: String, topK: Int): List<Hit> {
        val terms = tokenize(question).take(5)
        if (terms.isEmpty()) return emptyList()

        val pool = LinkedHashMap<String, ReferenceChunkEntity>()

        for (t in terms) {
            val hits = dao.searchChunksLike(t, limit = 60)
            hits.forEach { pool[it.id] = it }
        }

        if (pool.isEmpty()) return emptyList()

        val scored = pool.values.map { chunk ->
            val s = scoreChunk(chunk.text, terms)
            Hit(
                documentTitle = chunk.documentTitle,
                documentPath = chunk.documentPath,
                pageNumber = chunk.pageNumber,
                snippet = chunk.text,
                score = s
            )
        }.filter { it.score > 0 }
            .sortedWith(
                compareByDescending<Hit> { it.score }
                    .thenBy { it.documentTitle }
                    .thenBy { it.pageNumber }
            )

        return scored.take(topK)
    }

    fun buildMeadowContext(
        hits: List<Hit>,
        maxExcerptCharsPerHit: Int,
        maxTotalChars: Int
    ): String {
        if (hits.isEmpty()) return "NOT FOUND IN REFERENCES."

        val out = StringBuilder()
        var total = 0

        hits.forEachIndexed { idx, h ->
            if (total >= maxTotalChars) return@forEachIndexed

            val excerpt = h.snippet.trim()
            val clipped = excerpt.take(maxExcerptCharsPerHit)

            val block = buildString {
                appendLine("(${idx + 1}) ${h.documentTitle}")
                appendLine("Path: ${h.documentPath}")
                appendLine("Page: ${h.pageNumber}")
                appendLine("Excerpt:")
                appendLine(clipped)
                appendLine()
            }

            val remaining = maxTotalChars - total
            val toAppend = block.take(remaining)
            out.append(toAppend)
            total += toAppend.length
        }

        val final = out.toString().trim()
        return if (final.isBlank()) "NOT FOUND IN REFERENCES." else final
    }

    private fun tokenize(text: String): List<String> {
        val cleaned = text.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
        return cleaned.split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.length >= 3 }
            .distinct()
    }

    private fun scoreChunk(text: String, terms: List<String>): Int {
        val t = text.lowercase()
        var score = 0
        for (term in terms) {
            var idx = 0
            while (true) {
                idx = t.indexOf(term, idx)
                if (idx == -1) break
                score += 1
                idx += term.length
            }
        }
        return score
    }
}
