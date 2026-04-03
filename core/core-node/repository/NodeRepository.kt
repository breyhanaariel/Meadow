package com.meadow.core.node.repository

import com.meadow.core.node.domain.model.Node
import com.meadow.core.node.domain.model.NodeScope
import com.meadow.core.node.domain.model.NodeTree
import kotlinx.coroutines.flow.Flow

interface NodeRepository {
    suspend fun upsert(node: Node)
    suspend fun softDelete(nodeId: String, updatedAt: Long)
    suspend fun updateTitle(nodeId: String, title: String, updatedAt: Long)
    suspend fun updateContent(nodeId: String, content: String?, updatedAt: Long)
    suspend fun updateOrder(nodeId: String, orderKey: Long, updatedAt: Long)
    suspend fun move(nodeId: String, newParentNodeId: String?, updatedAt: Long)

    suspend fun get(nodeId: String): Node?
    fun observe(nodeId: String): Flow<Node?>

    suspend fun listByScope(scope: NodeScope, includeDeleted: Boolean = false): List<Node>
    fun observeByScope(scope: NodeScope, includeDeleted: Boolean = false): Flow<List<Node>>

    suspend fun getTree(scope: NodeScope, rootNodeId: String, includeDeleted: Boolean = false): NodeTree?
    fun observeTree(scope: NodeScope, rootNodeId: String, includeDeleted: Boolean = false): Flow<NodeTree?>
}
