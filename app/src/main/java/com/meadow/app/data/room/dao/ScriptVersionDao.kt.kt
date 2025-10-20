package com.meadow.app.data.room.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.meadow.app.data.room.entities.ScriptVersionEntity

@Dao
interface `ScriptVersionDao.kt` {
    @Query("SELECT * FROM script_versions WHERE scriptId = :scriptId ORDER BY timestamp DESC")
    fun getVersionsForScript(scriptId: String): Flow<List<ScriptVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(version: ScriptVersionEntity)

    @Query("DELETE FROM script_versions WHERE scriptId = :scriptId AND timestamp < :olderThan")
    suspend fun pruneOlder(scriptId: String, olderThan: Long)
}
