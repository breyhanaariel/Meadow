package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.app.data.room.entities.StoryboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryboardDao {
    @Query("SELECT * FROM storyboard_frame_table WHERE projectId=:projectId ORDER BY `index` ASC")
    fun observeForProject(projectId: String): Flow<List<StoryboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(frame: StoryboardEntity)

    @Delete
    suspend fun delete(frame: StoryboardEntity)
}
