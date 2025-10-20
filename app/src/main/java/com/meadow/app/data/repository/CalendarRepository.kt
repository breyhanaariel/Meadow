package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.CalendarDao
import com.meadow.app.data.room.entities.CalendarEventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val dao: CalendarDao
) {
    fun getEventsForProject(projectId: String): Flow<List<CalendarEventEntity>> =
        dao.getEventsForProject(projectId)

    suspend fun addEvent(event: CalendarEventEntity) = dao.insert(event)
    suspend fun deleteEvent(event: CalendarEventEntity) = dao.delete(event)
}
