package com.meadow.feature.script.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptVariantDao {

    @Query("SELECT * FROM script_variants WHERE scriptId = :scriptId ORDER BY lastEditedAt DESC")
    fun observeVariants(
        scriptId: String
    ): Flow<List<ScriptVariantEntity>>

    @Query("SELECT * FROM script_variants WHERE variantId = :variantId LIMIT 1")
    suspend fun getVariant(
        variantId: String
    ): ScriptVariantEntity?

    @Query("SELECT * FROM script_variants WHERE scriptId = :scriptId AND language = :language LIMIT 1")
    suspend fun getVariantByScriptAndLanguage(
        scriptId: String,
        language: String
    ): ScriptVariantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        entity: ScriptVariantEntity
    )

    @Update
    suspend fun update(
        entity: ScriptVariantEntity
    )
}
