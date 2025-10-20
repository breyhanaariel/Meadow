package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.TimelineDao
import com.meadow.app.data.room.entities.TimelineEventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TimelineRepository.kt
 *
 * Handles the chronological events of a story or production.
 */

@Singleton
class TimelineRepository @Inject constructor(
    private val dao: TimelineDao
) {

    /** Get all events for a project. */
    fun getEvents(projectId: String): Flow<List<TimelineEventEntity>> = dao.getEvents(projectId)

    /** Save or update an event. */
    suspend fun saveEvent(event: TimelineEventEntity) = dao.insert(event)

    /** Delete an event. */
    suspend fun deleteEvent(event: TimelineEventEntity) = dao.delete(event)
}
