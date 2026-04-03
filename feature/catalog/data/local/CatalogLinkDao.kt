package com.meadow.feature.catalog.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogLinkDao {

    @Query("SELECT * FROM catalog_links WHERE fromItemId = :itemId")
    fun observeLinksFrom(itemId: String): Flow<List<CatalogLinkEntity>>

    @Query("SELECT * FROM catalog_links WHERE toItemId = :itemId")
    fun observeLinksTo(itemId: String): Flow<List<CatalogLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(link: CatalogLinkEntity)

    @Delete
    suspend fun delete(link: CatalogLinkEntity)
}
