package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.app.data.room.entities.WikiEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WikiDao {
    @Query("SELECT * FROM wiki_entry_table WHERE projectId=:projectId ORDER BY lastModified DESC")
    fun observeForProject(projectId: String): Flow<List<WikiEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: WikiEntryEntity)

    @Delete
    suspend fun delete(entry: WikiEntryEntity)
}
