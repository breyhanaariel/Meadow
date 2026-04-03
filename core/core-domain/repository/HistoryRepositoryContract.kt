package com.meadow.core.domain.repository

import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType
import kotlinx.coroutines.flow.Flow

interface HistoryRepositoryContract {

    fun observeHistory(
        ownerId: String,
        ownerType: HistoryOwnerType
    ): Flow<List<HistoryEntry>>

    suspend fun insert(entry: HistoryEntry)

    suspend fun insertAll(entries: List<HistoryEntry>)

    suspend fun clearHistory(
        ownerId: String,
        ownerType: HistoryOwnerType
    )
}