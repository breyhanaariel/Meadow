package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationBlockStatusDao {

    @Query("SELECT * FROM script_translation_block_status WHERE targetVariantId = :targetVariantId")
    fun observeStatusesForTarget(
        targetVariantId: String
    ): Flow<List<TranslationBlockStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(
        entities: List<TranslationBlockStatusEntity>
    )

    @Query("DELETE FROM script_translation_block_status WHERE targetVariantId = :targetVariantId")
    suspend fun deleteForTarget(
        targetVariantId: String
    )
}
