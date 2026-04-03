package com.meadow.feature.calendar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {

    @Query("""
        SELECT * FROM calendar_events
        WHERE isDeleted = 0
        AND startAtMillis >= :fromMillis
        AND startAtMillis <= :toMillis
        ORDER BY startAtMillis ASC
    """)
    fun observeEventsBetween(
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<CalendarEventEntity>>

    @Query("""
        SELECT * FROM calendar_events
        WHERE localId = :localId
        LIMIT 1
    """)
    suspend fun getByLocalId(localId: String): CalendarEventEntity?

    @Query("""
        SELECT * FROM calendar_events
        WHERE googleEventId = :googleEventId
        LIMIT 1
    """)
    suspend fun getByGoogleId(googleEventId: String): CalendarEventEntity?

    @Query("""
        SELECT localId FROM calendar_events
        WHERE isDirty = 1
    """)
    suspend fun getDirtyIds(): List<String>

    @Query("""
        SELECT * FROM calendar_events
        WHERE localId IN (:ids)
    """)
    suspend fun getByIds(ids: List<String>): List<CalendarEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CalendarEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CalendarEventEntity>)

    @Query("""
        UPDATE calendar_events
        SET isDirty = :dirty,
            hasConflict = :hasConflict,
            lastSyncError = :lastError
        WHERE localId = :localId
    """)
    suspend fun updateFlags(
        localId: String,
        dirty: Boolean,
        hasConflict: Boolean,
        lastError: String?
    )

    @Query("""
        DELETE FROM calendar_events
        WHERE localId = :localId
    """)
    suspend fun deleteByLocalId(localId: String)

    @Transaction
    suspend fun upsertAndClean(entity: CalendarEventEntity) {
        upsert(entity.copy(isDirty = false, lastSyncError = null))
    }

    @Query("""
    SELECT COUNT(*) 
    FROM calendar_events 
    WHERE projectId = :projectId 
    AND isDeleted = 0
""")
    fun observeCountForProject(projectId: String): Flow<Int>
}
