package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.app.data.room.entities.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar_event_table WHERE projectId=:projectId ORDER BY date ASC")
    fun observeForProject(projectId: String): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: CalendarEventEntity)

    @Delete
    suspend fun delete(event: CalendarEventEntity)
}
