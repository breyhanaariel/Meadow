package com.meadow.core.database.history

import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType
import com.meadow.core.domain.model.HistorySource
import com.meadow.core.domain.repository.HistoryRepositoryContract
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class HistoryDatabaseAdapter @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepositoryContract {

    override fun observeHistory(
        ownerId: String,
        ownerType: HistoryOwnerType
    ): Flow<List<HistoryEntry>> =
        historyDao.observeHistory(
            ownerId = ownerId,
            ownerType = ownerType.name
        ).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insert(entry: HistoryEntry) {
        historyDao.insert(entry.toEntity())
    }

    override suspend fun insertAll(entries: List<HistoryEntry>) {
        historyDao.insertAll(entries.map { it.toEntity() })
    }

    override suspend fun clearHistory(
        ownerId: String,
        ownerType: HistoryOwnerType
    ) {
        historyDao.deleteForOwner(
            ownerId = ownerId,
            ownerType = ownerType.name
        )
    }

    private fun HistoryEntity.toDomain(): HistoryEntry =
        HistoryEntry(
            id = id,
            ownerId = ownerId,
            ownerType = HistoryOwnerType.valueOf(ownerType),
            fieldId = fieldId,
            oldValue = oldValue,
            newValue = newValue,
            parentFieldId = parentFieldId,
            source = HistorySource.valueOf(source),
            timestamp = timestamp,
            sessionId = sessionId
        )

    private fun HistoryEntry.toEntity(): HistoryEntity =
        HistoryEntity(
            id = id,
            ownerId = ownerId,
            ownerType = ownerType.name,
            fieldId = fieldId,
            oldValue = oldValue,
            newValue = newValue,
            parentFieldId = parentFieldId,
            source = source.name,
            timestamp = timestamp,
            sessionId = sessionId
        )
}