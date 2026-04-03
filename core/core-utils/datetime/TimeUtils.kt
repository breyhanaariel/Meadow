package com.meadow.core.utils.datetime

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    private val isoFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val displayFormatter = DateTimeFormatter.ofPattern("h:mm a")

    fun now(): String =
        LocalTime.now().format(isoFormatter)

    fun parse(iso: String): LocalTime? =
        runCatching { LocalTime.parse(iso, isoFormatter) }.getOrNull()

    fun format(time: LocalTime): String =
        time.format(isoFormatter)

    fun formatForDisplay(iso: String): String =
        parse(iso)?.format(displayFormatter) ?: iso

    fun toIso(hour12: Int, minute: Int, isAm: Boolean): String? {
        if (hour12 !in 1..12) return null
        if (minute !in 0..59) return null

        var hour24 = hour12 % 12
        if (!isAm) hour24 += 12

        return "%02d:%02d".format(hour24, minute)
    }

    fun fromIso(iso: String): Triple<Int, Int, Boolean>? {
        val time = parse(iso) ?: return null

        val isAm = time.hour < 12
        val hour12 = if (time.hour % 12 == 0) 12 else time.hour % 12

        return Triple(hour12, time.minute, isAm)
    }
}