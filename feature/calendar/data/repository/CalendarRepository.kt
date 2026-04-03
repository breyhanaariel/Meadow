package com.meadow.feature.calendar.data.repository

import com.meadow.feature.calendar.data.local.CalendarDao
import com.meadow.feature.calendar.data.mappers.CalendarEventMapper
import com.meadow.feature.calendar.domain.model.CalendarEvent
import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor(
    private val dao: CalendarDao
) : CalendarRepositoryContract {

    override fun observeEventsBetween(
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<CalendarEvent>> =
        dao.observeEventsBetween(fromMillis, toMillis)
            .map { list -> list.map(CalendarEventMapper::toDomain) }

    override suspend fun getDirtyIds(): List<String> =
        dao.getDirtyIds()

    override suspend fun createOrUpdateEvent(event: CalendarEvent): String {
        val now = System.currentTimeMillis()
        val localId = if (event.localId.isBlank()) UUID.randomUUID().toString() else event.localId

        dao.upsert(
            CalendarEventMapper.toEntity(
                event.copy(
                    localId = localId,
                    localUpdatedAt = now,
                    isDirty = true,
                    lastSyncError = null
                )
            )
        )
        return localId
    }

    override suspend fun markDeleted(localId: String) {
        val existing = dao.getByLocalId(localId) ?: return
        dao.upsert(
            existing.copy(
                isDeleted = true,
                isDirty = true,
                localUpdatedAt = System.currentTimeMillis(),
                lastSyncError = null
            )
        )
    }

    override suspend fun markSyncFailure(ids: List<String>, message: String) {
        ids.forEach { id ->
            dao.updateFlags(
                localId = id,
                dirty = true,
                hasConflict = false,
                lastError = message
            )
        }
    }

    override suspend fun upsertFromRemote(events: List<CalendarEvent>) {
        dao.upsertAll(events.map(CalendarEventMapper::toEntity))
    }
    override suspend fun deleteEvent(localId: String) {
        markDeleted(localId)
    }
    override fun observeCountForProject(projectId: String): Flow<Int> =
        dao.observeCountForProject(projectId)
}
