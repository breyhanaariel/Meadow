package com.meadow.feature.project.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects")
    fun observeAll(): kotlinx.coroutines.flow.Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun observeById(id: String): kotlinx.coroutines.flow.Flow<ProjectEntity?>
    @Query("""
        SELECT * FROM projects
        WHERE id = :id
    """)
    suspend fun getById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(project: ProjectEntity)

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("""
        SELECT DISTINCT p.*
        FROM projects p
        JOIN project_field_values fv
            ON fv.projectId = p.id
        WHERE fv.rawValue LIKE '%' || :query || '%'
        ORDER BY p.updatedAt DESC
    """)
    fun search(query: String): Flow<List<ProjectEntity>>


    @Query("""
        SELECT * FROM projects
        WHERE type IN (:types)
        ORDER BY updatedAt DESC
    """)
    fun filterByType(types: List<String>): Flow<List<ProjectEntity>>


    @Query("""
        SELECT DISTINCT p.*
        FROM projects p
        JOIN project_field_values fv
            ON fv.projectId = p.id
        WHERE fv.fieldId = :fieldId
          AND fv.rawValue = :value
        ORDER BY p.updatedAt DESC
    """)
    fun filterBySingleSelect(
        fieldId: String,
        value: String
    ): Flow<List<ProjectEntity>>


    @Query("""
        SELECT DISTINCT p.*
        FROM projects p
        JOIN project_field_values fv
            ON fv.projectId = p.id
        WHERE fv.fieldId = :fieldId
          AND fv.rawValue LIKE '%' || :value || '%'
        ORDER BY p.updatedAt DESC
    """)
    fun filterByMultiSelect(
        fieldId: String,
        value: String
    ): Flow<List<ProjectEntity>>


    @Query("""
        SELECT DISTINCT p.*
        FROM projects p

        LEFT JOIN project_field_values audience
            ON audience.projectId = p.id
            AND audience.fieldId = 'project.audience'

        LEFT JOIN project_field_values genre
            ON genre.projectId = p.id
            AND genre.fieldId = 'project.genre'

        LEFT JOIN project_field_values elements
            ON elements.projectId = p.id
            AND elements.fieldId = 'project.elements'

        LEFT JOIN project_field_values rating
            ON rating.projectId = p.id
            AND rating.fieldId = 'project.rating'

        LEFT JOIN project_field_values warnings
            ON warnings.projectId = p.id
            AND warnings.fieldId = 'project.warnings'

        LEFT JOIN project_field_values status
            ON status.projectId = p.id
            AND status.fieldId = 'project.status'

        LEFT JOIN project_field_values format
            ON format.projectId = p.id
            AND format.fieldId = 'project.format'

        WHERE
            (:type IS NULL OR p.type = :type)
            AND (:audience IS NULL OR audience.rawValue LIKE '%' || :audience || '%')
            AND (:genre IS NULL OR genre.rawValue LIKE '%' || :genre || '%')
            AND (:elements IS NULL OR elements.rawValue LIKE '%' || :elements || '%')
            AND (:rating IS NULL OR rating.rawValue = :rating)
            AND (:warnings IS NULL OR warnings.rawValue LIKE '%' || :warnings || '%')
            AND (:status IS NULL OR status.rawValue = :status)
            AND (:format IS NULL OR format.rawValue = :format)

        ORDER BY
            CASE WHEN :sort = 'updated' THEN p.updatedAt END DESC,
            CASE WHEN :sort = 'created' THEN p.startDate END DESC
    """)
    fun filterList(
        type: String?,
        audience: String?,
        genre: String?,
        elements: String?,
        rating: String?,
        warnings: String?,
        status: String?,
        format: String?,
        sort: String
    ): Flow<List<ProjectEntity>>


    @Query("""
        SELECT * FROM projects
        WHERE seriesId = :seriesId
        ORDER BY updatedAt DESC
    """)
    fun projectsInSeries(seriesId: String): Flow<List<ProjectEntity>>


    @Query("""
        SELECT * FROM projects
        ORDER BY updatedAt DESC
    """)
    fun sortByUpdated(): Flow<List<ProjectEntity>>

    @Query("""
        SELECT * FROM projects
        ORDER BY startDate DESC
    """)
    fun sortByCreated(): Flow<List<ProjectEntity>>

    @Query("""SELECT * FROM projects WHERE isDirty = 1""")
    suspend fun getDirtyProjects(): List<ProjectEntity>

    @Query("""SELECT id FROM projects WHERE isDirty = 1""")
    suspend fun getDirtyProjectIds(): List<String>

    @Query("""
        UPDATE projects
        SET
            isDirty = :isDirty,
            lastSyncError = :error,
            retryCount = :retryCount
        WHERE id = :projectId
    """)
    suspend fun updateDirtyAndError(
        projectId: String,
        isDirty: Int,
        error: String?,
        retryCount: Int
    )

    @Query("""
        UPDATE projects
        SET
            remoteVersion = :remoteVersion,
            remoteUpdatedAt = :remoteUpdatedAt,
            lastFirestoreSyncAt = :lastFirestoreSyncAt,
            isDirty = :isDirty,
            hasConflict = :hasConflict,
            lastSyncError = :error,
            retryCount = :retryCount
        WHERE id = :projectId
    """)
    suspend fun updateAfterFirestoreSync(
        projectId: String,
        remoteVersion: Long,
        remoteUpdatedAt: Long?,
        lastFirestoreSyncAt: Long?,
        isDirty: Int,
        hasConflict: Int,
        error: String?,
        retryCount: Int
    )

    @Query("""
        UPDATE projects
        SET seriesId = :seriesId
        WHERE id = :projectId
    """)
    suspend fun updateSeries(
        projectId: String,
        seriesId: String?
    )
}
