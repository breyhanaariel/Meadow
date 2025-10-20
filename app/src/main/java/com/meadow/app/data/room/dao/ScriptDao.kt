package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.data.room.entities.ScriptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {
    @Query("SELECT * FROM script_table WHERE projectId=:projectId ORDER BY lastModified DESC")
    fun observeByProject(projectId: String): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM script_table WHERE id=:id LIMIT 1")
    suspend fun getById(id: String): ScriptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(script: ScriptEntity)

    @Delete
    suspend fun delete(script: ScriptEntity)
}
