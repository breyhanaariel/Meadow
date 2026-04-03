package com.meadow.feature.catalog.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogItemDao {

    @Query("DELETE FROM catalog_items")
    suspend fun clearAll()

    @Query("SELECT * FROM catalog_items")
    suspend fun getAllCatalogOnce(): List<CatalogItemEntity>

    @Query("SELECT * FROM catalog_items")
    fun observeAllCatalog(): Flow<List<CatalogItemEntity>>

    @Query("SELECT * FROM catalog_items WHERE id = :id")
    fun observeById(id: String): Flow<CatalogItemEntity?>

    @Query("SELECT * FROM catalog_items WHERE projectId = :projectId")
    fun observeByProject(projectId: String): Flow<List<CatalogItemEntity>>

    @Query("SELECT * FROM catalog_items WHERE seriesId = :seriesId")
    fun observeBySeries(seriesId: String): Flow<List<CatalogItemEntity>>

    @Query("SELECT * FROM catalog_items WHERE id = :id")
    suspend fun getCatalogItemById(id: String): CatalogItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CatalogItemEntity)

    @Delete
    suspend fun deleteCatalogItem(entity: CatalogItemEntity)

    @Query("SELECT id FROM catalog_items WHERE isDirty = 1")
    suspend fun getDirtyItemIds(): List<String>

    @Query("""
        UPDATE catalog_items
        SET lastSyncError = :error,
            retryCount = retryCount + 1
        WHERE id IN (:ids)
    """)
    suspend fun markSyncFailure(
        ids: List<String>,
        error: String
    )

    @Query("SELECT COUNT(*) FROM catalog_items WHERE projectId = :projectId")
    fun observeCountForProject(projectId: String): Flow<Int>
}
