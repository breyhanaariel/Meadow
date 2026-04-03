package com.meadow.feature.project.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectHistoryDao {

    @Query("""
        SELECT * FROM project_history
        WHERE projectId = :projectId
        ORDER BY timestamp DESC
    """)
    fun observeHistory(projectId: String): Flow<List<ProjectHistoryEntity>>

    @Query("SELECT * FROM project_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProjectHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ProjectHistoryEntity)

    @Query("DELETE FROM project_history WHERE projectId = :projectId")
    suspend fun deleteForProject(projectId: String)
}