package com.meadow.core.node.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeEdgeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NodeEdgeEntity)

    @Query("SELECT * FROM node_edges WHERE edgeId = :edgeId LIMIT 1")
    suspend fun get(edgeId: String): NodeEdgeEntity?

    @Query("SELECT * FROM node_edges WHERE edgeId = :edgeId LIMIT 1")
    fun observe(edgeId: String): Flow<NodeEdgeEntity?>

    @Query(
        "SELECT * FROM node_edges WHERE (fromNodeId = :nodeId OR toNodeId = :nodeId) AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY updatedAt DESC"
    )
    suspend fun listByNode(nodeId: String, includeDeleted: Boolean): List<NodeEdgeEntity>

    @Query(
        "SELECT * FROM node_edges WHERE (fromNodeId = :nodeId OR toNodeId = :nodeId) AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY updatedAt DESC"
    )
    fun observeByNode(nodeId: String, includeDeleted: Boolean): Flow<List<NodeEdgeEntity>>

    @Query("UPDATE node_edges SET isDeleted = 1, updatedAt = :updatedAt, isDirty = 1, localVersion = localVersion + 1 WHERE edgeId = :edgeId")
    suspend fun softDelete(edgeId: String, updatedAt: Long)
}
