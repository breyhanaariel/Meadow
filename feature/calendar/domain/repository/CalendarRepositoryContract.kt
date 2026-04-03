package com.meadow.feature.calendar.domain.repository

import com.meadow.feature.calendar.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface CalendarRepositoryContract {

    fun observeEventsBetween(
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<CalendarEvent>>

    suspend fun getDirtyIds(): List<String>

    suspend fun createOrUpdateEvent(event: CalendarEvent): String

    suspend fun markDeleted(localId: String)

    suspend fun markSyncFailure(ids: List<String>, message: String)

    suspend fun upsertFromRemote(events: List<CalendarEvent>)
    suspend fun deleteEvent(localId: String)

    fun observeCountForProject(projectId: String): Flow<Int>
}
