package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {

    @Query("SELECT * FROM scripts WHERE projectId = :projectId ORDER BY updatedAt DESC")
    fun observeScriptsByProject(
        projectId: String
    ): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts WHERE scriptId = :scriptId LIMIT 1")
    suspend fun getScript(
        scriptId: String
    ): ScriptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        entity: ScriptEntity
    )

    @Update
    suspend fun update(
        entity: ScriptEntity
    )
}
