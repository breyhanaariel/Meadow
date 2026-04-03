package com.meadow.feature.common.api

import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType

interface HistoryLabelResolver {
    fun supports(ownerType: HistoryOwnerType): Boolean
    fun resolve(entry: HistoryEntry): String
}