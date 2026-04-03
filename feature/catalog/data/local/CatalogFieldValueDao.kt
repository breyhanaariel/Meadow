package com.meadow.feature.catalog.data.local

import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface CatalogFieldValueDao {

    @Query("DELETE FROM catalog_field_values")
    suspend fun clearAll()

    @Query("SELECT * FROM catalog_field_values WHERE catalogItemId = :itemId")
    suspend fun getForItem(itemId: String): List<CatalogFieldValueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(values: List<CatalogFieldValueEntity>)

    @Query("DELETE FROM catalog_field_values WHERE catalogItemId = :itemId")
    suspend fun deleteForItem(itemId: String)

    @Query("SELECT * FROM catalog_field_values WHERE catalogItemId = :itemId")
    fun observeForItem(itemId: String): Flow<List<CatalogFieldValueEntity>>
}
