package com.meadow.core.media.player.subtitle

object SubtitleParser {

    fun parseSrt(content: String): List<Pair<Double, String>> {
        val entries = content.split("\n\n")
        val results = mutableListOf<Pair<Double, String>>()

        for (entry in entries) {
            val lines = entry.lines()
            if (lines.size >= 3) {
                val timeLine = lines[1] // e.g. 00:01:30,000 --> 00:01:35,000
                val text = lines.drop(2).joinToString(" ")
                val startTime = timeLine.substringBefore(" --> ").toSeconds()
                results.add(startTime to text)
            }
        }
        return results
    }

    private fun String.toSeconds(): Double {
        val parts = split(":", ",")
        return parts[0].toDouble() * 3600 +
                parts[1].toDouble() * 60 +
                parts[2].toDouble() +
                parts[3].toDouble() / 1000
    }
}