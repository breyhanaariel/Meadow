package com.meadow.core.utils.extensions

fun Long.toReadableSize(): String = when {
    this >= 1_000_000 -> String.format("%.2f MB", this / 1_000_000.0)
    this >= 1_000     -> String.format("%.2f KB", this / 1_000.0)
    else -> "$this B"
}