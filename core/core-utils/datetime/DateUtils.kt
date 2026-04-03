package com.meadow.core.utils.datetime

import java.time.*
import java.time.format.DateTimeFormatter

object DateUtils {

    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    fun today(): String =
        LocalDate.now().format(isoFormatter)

    fun format(date: LocalDate): String =
        date.format(isoFormatter)

    fun parse(date: String): LocalDate? =
        runCatching { LocalDate.parse(date, isoFormatter) }.getOrNull()

    fun formatForDisplay(iso: String): String =
        parse(iso)?.format(displayFormatter) ?: iso

    fun isoToMillis(iso: String): Long? =
        parse(iso)
            ?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()

    fun millisToIso(millis: Long): String =
        Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(isoFormatter)

    fun normalize(input: String): String? =
        parse(input)?.format(isoFormatter)

    fun millisToDisplay(millis: Long?): String {
        if (millis == null) return ""
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(displayFormatter)
    }
}