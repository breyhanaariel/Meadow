package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScriptBlockAnchorDao {

    @Query("SELECT * FROM script_block_anchors WHERE variantId = :variantId ORDER BY orderKey ASC")
    suspend fun getAnchorsForVariant(
        variantId: String
    ): List<ScriptBlockAnchorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(
        entities: List<ScriptBlockAnchorEntity>
    )

    @Query("DELETE FROM script_block_anchors WHERE variantId = :variantId")
    suspend fun deleteForVariant(
        variantId: String
    )
}
