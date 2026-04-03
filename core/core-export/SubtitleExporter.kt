package com.meadow.core.export

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object SubtitleExporter {
    suspend fun exportVtt(subs: List<Pair<Double, String>>, outFile: File) =
        withContext(Dispatchers.IO) {
            outFile.printWriter().use { pw ->
                pw.println("WEBVTT\n")
                subs.forEachIndexed { i, (time, text) ->
                    val timestamp = formatTime(time)
                    pw.println("${i + 1}")
                    pw.println("$timestamp --> ${formatTime(time + 2)}")
                    pw.println(text)
                    pw.println()
                }
            }
        }

    private fun formatTime(seconds: Double): String {
        val s = seconds.toInt()
        val ms = ((seconds - s) * 1000).toInt()
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return "%02d:%02d:%02d.%03d".format(h, m, sec, ms)
    }
}