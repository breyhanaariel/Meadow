package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptAssetDao {

    @Query("SELECT * FROM script_assets WHERE scriptId = :scriptId")
    fun observeAssets(
        scriptId: String
    ): Flow<List<ScriptAssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        entity: ScriptAssetEntity
    )

    @Query("DELETE FROM script_assets WHERE assetId = :assetId")
    suspend fun delete(
        assetId: String
    )
}
