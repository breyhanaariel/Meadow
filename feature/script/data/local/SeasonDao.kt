package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {

    @Query("SELECT * FROM seasons WHERE projectId = :projectId ORDER BY `order` ASC")
    fun observeSeasonsByProject(
        projectId: String
    ): Flow<List<SeasonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        entity: SeasonEntity
    )
}
