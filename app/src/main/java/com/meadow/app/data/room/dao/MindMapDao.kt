package com.meadow.app.data.room.dao

import androidx.room.*
import com.meadow.app.data.room.entities.MindMapNodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MindMapDao {
    @Query("SELECT * FROM mindmap_node_table WHERE projectId=:projectId")
    fun observeForProject(projectId: String): Flow<List<MindMapNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(node: MindMapNodeEntity)

    @Delete
    suspend fun delete(node: MindMapNodeEntity)
}
