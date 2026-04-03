package com.meadow.feature.common.api

import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType

interface HistoryRestoreHandler {
    fun supports(ownerType: HistoryOwnerType): Boolean
    suspend fun restore(entry: HistoryEntry)
}