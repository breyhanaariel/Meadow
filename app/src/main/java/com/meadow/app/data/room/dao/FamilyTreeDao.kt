package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.app.data.room.entities.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar_events WHERE projectId = :projectId ORDER BY startTime ASC")
    fun getEventsForProject(projectId: String): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CalendarEventEntity)

    @Delete
    suspend fun delete(event: CalendarEventEntity)
}
