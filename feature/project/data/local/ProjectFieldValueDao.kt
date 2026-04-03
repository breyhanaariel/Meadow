package com.meadow.feature.project.data.local

import androidx.room.*

@Dao
interface ProjectFieldValueDao {

    @Query("""
        SELECT * FROM project_field_values
        WHERE projectId = :projectId
    """)
    fun observeValuesForProject(projectId: String): kotlinx.coroutines.flow.Flow<List<ProjectFieldValueEntity>>

    @Query("""
        SELECT * FROM project_field_values
        WHERE projectId = :projectId
    """)
    suspend fun valuesForProject(projectId: String): List<ProjectFieldValueEntity>

    @Query("SELECT * FROM project_field_values")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<ProjectFieldValueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(value: ProjectFieldValueEntity)
}
