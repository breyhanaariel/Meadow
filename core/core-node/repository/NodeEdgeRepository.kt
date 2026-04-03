package com.meadow.core.node.repository

import com.meadow.core.node.domain.model.NodeEdge
import kotlinx.coroutines.flow.Flow

interface NodeEdgeRepository {
    suspend fun upsert(edge: NodeEdge)
    suspend fun softDelete(edgeId: String, updatedAt: Long)

    suspend fun get(edgeId: String): NodeEdge?
    fun observe(edgeId: String): Flow<NodeEdge?>

    suspend fun listByNode(nodeId: String, includeDeleted: Boolean = false): List<NodeEdge>
    fun observeByNode(nodeId: String, includeDeleted: Boolean = false): Flow<List<NodeEdge>>
}
