package com.meadow.core.data.fields.repository

import com.meadow.core.data.fields.FieldChange
import com.meadow.core.data.fields.FieldDiffEngine
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType
import com.meadow.core.domain.model.HistorySource
import com.meadow.core.domain.repository.HistoryRepositoryContract
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class FieldHistoryRepository @Inject constructor(
    private val historyRepositoryContract: HistoryRepositoryContract
) {

    fun observeHistory(
        ownerId: String,
        ownerType: HistoryOwnerType
    ): Flow<List<HistoryEntry>> =
        historyRepositoryContract.observeHistory(
            ownerId = ownerId,
            ownerType = ownerType
        )

    suspend fun recordFieldDiff(
        ownerId: String,
        ownerType: HistoryOwnerType,
        oldValues: List<FieldValue>,
        newValues: List<FieldValue>,
        source: HistorySource = HistorySource.MANUAL,
        timestamp: Long = System.currentTimeMillis(),
        sessionId: String = UUID.randomUUID().toString()
    ) {
        val changes = FieldDiffEngine.diffFieldValues(
            oldValues = oldValues,
            newValues = newValues
        )

        insertChanges(
            ownerId = ownerId,
            ownerType = ownerType,
            changes = changes,
            source = source,
            timestamp = timestamp,
            sessionId = sessionId
        )
    }

    suspend fun recordMapDiff(
        ownerId: String,
        ownerType: HistoryOwnerType,
        oldValues: Map<String, String?>,
        newValues: Map<String, String?>,
        source: HistorySource = HistorySource.MANUAL,
        timestamp: Long = System.currentTimeMillis(),
        sessionId: String = UUID.randomUUID().toString()
    ) {
        val changes = FieldDiffEngine.diffMaps(
            oldValues = oldValues,
            newValues = newValues
        )

        insertChanges(
            ownerId = ownerId,
            ownerType = ownerType,
            changes = changes,
            source = source,
            timestamp = timestamp,
            sessionId = sessionId
        )
    }

    suspend fun recordSingleChange(
        ownerId: String,
        ownerType: HistoryOwnerType,
        fieldId: String,
        oldValue: String?,
        newValue: String?,
        parentFieldId: String? = null,
        source: HistorySource = HistorySource.MANUAL,
        timestamp: Long = System.currentTimeMillis(),
        sessionId: String = UUID.randomUUID().toString()
    ) {
        val normalizedOld = oldValue.normalizeForHistory()
        val normalizedNew = newValue.normalizeForHistory()

        if (normalizedOld == normalizedNew) return

        historyRepositoryContract.insert(
            HistoryEntry(
                id = UUID.randomUUID().toString(),
                ownerId = ownerId,
                ownerType = ownerType,
                fieldId = fieldId,
                oldValue = normalizedOld,
                newValue = normalizedNew,
                parentFieldId = parentFieldId,
                source = source,
                timestamp = timestamp,
                sessionId = sessionId
            )
        )
    }

    suspend fun clearHistory(
        ownerId: String,
        ownerType: HistoryOwnerType
    ) {
        historyRepositoryContract.clearHistory(
            ownerId = ownerId,
            ownerType = ownerType
        )
    }

    private suspend fun insertChanges(
        ownerId: String,
        ownerType: HistoryOwnerType,
        changes: List<FieldChange>,
        source: HistorySource,
        timestamp: Long,
        sessionId: String
    ) {
        if (changes.isEmpty()) return

        historyRepositoryContract.insertAll(
            changes.map { change ->
                HistoryEntry(
                    id = UUID.randomUUID().toString(),
                    ownerId = ownerId,
                    ownerType = ownerType,
                    fieldId = change.fieldId,
                    oldValue = change.oldValue,
                    newValue = change.newValue,
                    parentFieldId = change.parentFieldId,
                    source = source,
                    timestamp = timestamp,
                    sessionId = sessionId
                )
            }
        )
    }

    private fun String?.normalizeForHistory(): String? =
        this
            ?.trim()
            ?.ifBlank { null }
}