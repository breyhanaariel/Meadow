package com.meadow.core.ai.data.context

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AiContextDao {

    @Query("SELECT * FROM ai_contexts WHERE id = :id LIMIT 1")
    suspend fun getContextById(id: String): AiContextEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AiContextEntity)

    @Query("DELETE FROM ai_contexts WHERE id = :id")
    suspend fun deleteContextById(id: String)

    @Query(
        """
        SELECT * FROM ai_contexts
        WHERE scopeKey IS NULL AND enabled = 1
        ORDER BY pinned DESC, updatedAtUtcMs DESC
        """
    )
    suspend fun listEnabledGlobalContexts(): List<AiContextEntity>

    @Query(
        """
        SELECT * FROM ai_contexts
        WHERE scopeKey = :scopeKey AND enabled = 1
        ORDER BY pinned DESC, updatedAtUtcMs DESC
        """
    )
    suspend fun listEnabledScopedContexts(scopeKey: String): List<AiContextEntity>

    @Query(
        """
        SELECT * FROM ai_contexts
        WHERE scopeKey IS NULL
        ORDER BY pinned DESC, updatedAtUtcMs DESC
        """
    )
    suspend fun listGlobalContextsAll(): List<AiContextEntity>

    @Query(
        """
        SELECT * FROM ai_contexts
        WHERE scopeKey = :scopeKey
        ORDER BY pinned DESC, updatedAtUtcMs DESC
        """
    )
    suspend fun listScopedContextsAll(scopeKey: String): List<AiContextEntity>

    @Query(
        """
    SELECT * FROM ai_contexts
    WHERE (:scopeKey IS NULL OR scopeKey = :scopeKey)
    ORDER BY pinned DESC, updatedAtUtcMs DESC
    """
    )
    suspend fun listForUi(scopeKey: String?): List<AiContextEntity>

}
