package com.meadow.core.ai.engine.bloom

object BloomChunker {

    private const val MAX_CHARS = 3500

    fun chunk(text: String): List<String> {
        val cleaned = text.trim()

        if (cleaned.length <= MAX_CHARS) {
            return listOf(cleaned)
        }

        val chunks = mutableListOf<String>()
        var start = 0

        while (start < cleaned.length) {
            val end = (start + MAX_CHARS).coerceAtMost(cleaned.length)
            val boundary = findBoundary(cleaned, start, end)
            chunks.add(cleaned.substring(start, boundary).trim())
            start = boundary
        }

        return chunks
    }

    private fun findBoundary(text: String, start: Int, hardEnd: Int): Int {
        val slice = text.substring(start, hardEnd)

        // Paragraph break detection
        val paragraphBreak = slice.lastIndexOf("\n\n")
        if (paragraphBreak > 0) {
            return start + paragraphBreak
        }

        //  Sentence boundary fallback
        val period = slice.lastIndexOf('.')
        val question = slice.lastIndexOf('?')
        val exclaim = slice.lastIndexOf('!')
        val newline = slice.lastIndexOf('\n')

        val boundary = listOf(period, question, exclaim, newline)
            .filter { it > 0 }
            .maxOrNull()

        return if (boundary != null) {
            start + boundary + 1
        } else {
            hardEnd
        }
    }

    fun mergeForFinalPass(chunks: List<String>): String {
        return chunks.joinToString("\n\n")
    }
}
