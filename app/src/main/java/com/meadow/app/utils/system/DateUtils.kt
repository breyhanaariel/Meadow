package com.meadow.utils.system

import java.text.SimpleDateFormat
import java.util.*

/**
 * DateUtils.kt
 *
 * Provides date parsing and pretty-formatting helpers.
 */
object DateUtils {

    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    private val prettyFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

    fun currentDate(): String = isoFormatter.format(Date())

    fun formatPretty(dateString: String): String = try {
        prettyFormatter.format(isoFormatter.parse(dateString)!!)
    } catch (_: Exception) {
        dateString
    }
}
