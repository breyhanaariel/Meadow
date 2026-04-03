package com.meadow.core.ai.engine.personas.petal

object PetalChunker {

    private const val MAX_CHARS = 8000
    private val sentenceRegex = Regex("""(?<=[.!?])\s+""")

    fun chunk(text: String): List<String> {
        val trimmed = text.trim()

        if (trimmed.length <= MAX_CHARS) {
            return listOf(trimmed)
        }

        val chunks = mutableListOf<String>()
        var remaining = trimmed

        while (remaining.length > MAX_CHARS) {
            val soft = remaining.substring(0, MAX_CHARS)

            val paragraphBreak = soft.lastIndexOf("\n\n")
            val paragraphIndex = if (paragraphBreak > 0) paragraphBreak else null

            val sentenceBoundary = sentenceRegex.findAll(soft)
                .lastOrNull()?.range?.last

            val cutIndex =
                paragraphIndex
                    ?: sentenceBoundary
                    ?: MAX_CHARS

            val chunk = remaining.substring(0, cutIndex).trim()
            chunks.add(chunk)

            remaining = remaining.substring(cutIndex).trimStart()
        }

        if (remaining.isNotBlank()) {
            chunks.add(remaining)
        }

        return chunks
    }
}
