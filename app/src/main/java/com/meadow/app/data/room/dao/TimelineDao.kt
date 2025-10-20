package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.app.data.room.entities.TimelineEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {
    @Query("SELECT * FROM timeline_event_table WHERE projectId=:projectId ORDER BY occursAt ASC")
    fun observeForProject(projectId: String): Flow<List<TimelineEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: TimelineEventEntity)

    @Delete
    suspend fun delete(event: TimelineEventEntity)
}
