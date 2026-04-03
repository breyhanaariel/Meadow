package com.meadow.feature.catalog.ui.util

import com.meadow.feature.catalog.internal.schema.CatalogSchemaRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogSchemaBootstrap @Inject constructor(
    private val schemaRegistry: CatalogSchemaRegistry
) {

    private var initialized = false

    fun ensureInitialized() {
        if (initialized) return
        initialized = true

        schemaRegistry.initializeIfNeeded()
    }
}
