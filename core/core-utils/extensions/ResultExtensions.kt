package com.meadow.core.utils.extensions

inline fun <T> runCatchingOrNull(block: () -> T): T? =
    runCatching { block() }.getOrNull()