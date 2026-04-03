package com.meadow.feature.catalog.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CatalogSyncRunner(
    private val scope: CoroutineScope,
    private val onStart: () -> Unit,
    private val onSuccess: () -> Unit,
    private val onError: (Throwable) -> Unit,
    private val block: suspend () -> Unit
) {
    fun run() {
        scope.launch {
            onStart()
            try {
                block()
                onSuccess()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }
}
