package com.meadow.core.utils.extensions

fun <T> List<T>.safeGet(index: Int): T? =
    if (index in indices) this[index] else null