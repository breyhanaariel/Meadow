package com.meadow.core.utils.coroutines


inline fun <T> runBlockingSafe(block: () -> T): T = block()