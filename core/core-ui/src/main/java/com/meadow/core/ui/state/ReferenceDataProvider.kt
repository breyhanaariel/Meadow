package com.meadow.core.ui.state

import com.meadow.core.ui.state.ReferenceItem
import kotlinx.coroutines.flow.Flow

interface ReferenceDataProvider {
    fun observeItems(schemaKey: String): Flow<List<ReferenceItem>>
}