package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptMediaTrackDao {

    @Query("SELECT * FROM script_media_tracks WHERE scriptId = :scriptId")
    fun observeTracks(
        scriptId: String
    ): Flow<List<ScriptMediaTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        entity: ScriptMediaTrackEntity
    )

    @Query("DELETE FROM script_media_tracks WHERE trackId = :trackId")
    suspend fun delete(
        trackId: String
    )
}
