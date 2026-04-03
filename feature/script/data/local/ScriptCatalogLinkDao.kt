package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptCatalogLinkDao {

    @Query("SELECT * FROM script_catalog_links WHERE variantId = :variantId")
    fun observeLinksForVariant(
        variantId: String
    ): Flow<List<ScriptCatalogLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        entity: ScriptCatalogLinkEntity
    )

    @Query("DELETE FROM script_catalog_links WHERE id = :id")
    suspend fun deleteById(
        id: String
    )
}
