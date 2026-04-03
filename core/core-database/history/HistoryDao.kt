package com.meadow.core.database.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("""
        SELECT * FROM history
        WHERE ownerId = :ownerId
        AND ownerType = :ownerType
        ORDER BY timestamp DESC, id DESC
    """)
    fun observeHistory(
        ownerId: String,
        ownerType: String
    ): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<HistoryEntity>)

    @Query("""
        DELETE FROM history
        WHERE ownerId = :ownerId
        AND ownerType = :ownerType
    """)
    suspend fun deleteForOwner(
        ownerId: String,
        ownerType: String
    )
}