package com.meadow.core.utils.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CoroutineUtils {
    val IO: CoroutineDispatcher get() = Dispatchers.IO
    val Main: CoroutineDispatcher get() = Dispatchers.Main
    val Default: CoroutineDispatcher get() = Dispatchers.Default

    suspend fun <T> io(block: suspend () -> T): T =
        withContext(IO) { block() }

    suspend fun <T> main(block: suspend () -> T): T =
        withContext(Main) { block() }
}