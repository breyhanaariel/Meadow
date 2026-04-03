package com.meadow.core.media.utils

object FormatUtils {

    fun formatDuration(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    fun formatResolution(width: Int, height: Int): String {
        return "${width}x${height}"
    }
}