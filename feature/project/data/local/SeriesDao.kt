package com.meadow.feature.project.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {

    @Query("SELECT * FROM series ORDER BY title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<SeriesEntity?>

    @Query("SELECT * FROM series ORDER BY title COLLATE NOCASE ASC")
    suspend fun getAll(): List<SeriesEntity>

    @Query("SELECT * FROM series WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SeriesEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SeriesEntity)

    @Query(""" UPDATE series SET title = :newTitle WHERE id = :seriesId """)
    suspend fun updateTitle(
        seriesId: String,
        newTitle: String
    )

    @Query(""" UPDATE series SET sharedFieldIds = :sharedFieldIds WHERE id = :seriesId """)
    suspend fun updateSharedFields(
        seriesId: String,
        sharedFieldIds: String
    )

    @Query("UPDATE series SET sharedCatalogFieldIds = :sharedCatalogFieldIds WHERE id = :seriesId")
    suspend fun updateSharedCatalogFields(seriesId: String, sharedCatalogFieldIds: String)

    @Query(""" UPDATE series SET title = :title, projectIds = :projectIds, sharedFieldIds = :sharedFieldIds, sharedCatalogFieldIds = :sharedCatalogFieldIds WHERE id = :seriesId """)
    suspend fun updateSeries(
        seriesId: String,
        title: String,
        projectIds: String,
        sharedFieldIds: String,
        sharedCatalogFieldIds: String
    )

    @Query(""" UPDATE series SET sharedCatalogFieldIds = :sharedCatalogFieldIds WHERE id = :seriesId """)
    suspend fun updateSeriesCatalogFields(
        seriesId: String,
        sharedCatalogFieldIds: String
    )

    @Query("DELETE FROM series WHERE id = :seriesId")
    suspend fun deleteSeries(seriesId: String)
}
