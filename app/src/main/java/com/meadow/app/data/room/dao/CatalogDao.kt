package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.app.data.room.entities.CatalogItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    @Query("SELECT * FROM catalog_item_table WHERE projectId=:projectId ORDER BY updatedAt DESC")
    fun observeForProject(projectId: String): Flow<List<CatalogItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CatalogItemEntity)

    @Delete
    suspend fun delete(item: CatalogItemEntity)
}
