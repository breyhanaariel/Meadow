package com.meadow.feature.project.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectFieldHistoryDao {

    @Query("""
        SELECT * FROM project_field_history
        WHERE projectId = :projectId
        ORDER BY changedAt DESC
    """)
    fun observeProjectHistory(
        projectId: String
    ): Flow<List<ProjectFieldHistoryEntity>>

    @Query("""
        SELECT * FROM project_field_history
        WHERE projectId = :projectId
        AND field = :fieldId
        ORDER BY changedAt DESC
    """)
    fun observeFieldHistory(
        projectId: String,
        fieldId: String
    ): Flow<List<ProjectFieldHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ProjectFieldHistoryEntity)

    @Query("DELETE FROM project_field_history WHERE projectId = :projectId")
    suspend fun deleteByProjectId(projectId: String)

    @Query("""
        DELETE FROM project_field_history
        WHERE projectId = :projectId
        AND field = :fieldId
    """)
    suspend fun deleteByProjectAndField(
        projectId: String,
        fieldId: String
    )
}
