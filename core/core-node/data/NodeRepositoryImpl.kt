package com.meadow.core.node.data

import com.meadow.core.node.domain.model.Node
import com.meadow.core.node.domain.model.NodeScope
import com.meadow.core.node.domain.model.NodeTree
import com.meadow.core.node.repository.NodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NodeRepositoryImpl @Inject constructor(
    private val nodeDao: NodeDao
) : NodeRepository {

    override suspend fun upsert(node: Node) {
        nodeDao.upsert(node.toEntity())
    }

    override suspend fun softDelete(nodeId: String, updatedAt: Long) {
        nodeDao.softDelete(nodeId, updatedAt)
    }

    override suspend fun updateTitle(nodeId: String, title: String, updatedAt: Long) {
        nodeDao.updateTitle(nodeId, title, updatedAt)
    }

    override suspend fun updateContent(nodeId: String, content: String?, updatedAt: Long) {
        nodeDao.updateContent(nodeId, content, updatedAt)
    }

    override suspend fun updateOrder(nodeId: String, orderKey: Long, updatedAt: Long) {
        nodeDao.updateOrder(nodeId, orderKey, updatedAt)
    }

    override suspend fun move(nodeId: String, newParentNodeId: String?, updatedAt: Long) {
        nodeDao.updateParent(nodeId, newParentNodeId, updatedAt)
    }

    override suspend fun get(nodeId: String): Node? =
        nodeDao.get(nodeId)?.toDomain()

    override fun observe(nodeId: String): Flow<Node?> =
        nodeDao.observe(nodeId).map { it?.toDomain() }

    override suspend fun listByScope(scope: NodeScope, includeDeleted: Boolean): List<Node> =
        when (scope) {
            is NodeScope.Project -> nodeDao.listByProject(scope.projectId, includeDeleted).map { it.toDomain() }
            is NodeScope.Series -> nodeDao.listBySeries(scope.seriesId, includeDeleted).map { it.toDomain() }
            is NodeScope.ProjectInSeries -> nodeDao.listByProjectInSeries(scope.projectId, scope.seriesId, includeDeleted).map { it.toDomain() }
        }

    override fun observeByScope(scope: NodeScope, includeDeleted: Boolean): Flow<List<Node>> =
        when (scope) {
            is NodeScope.Project -> nodeDao.observeByProject(scope.projectId, includeDeleted).map { it.map(NodeEntity::toDomain) }
            is NodeScope.Series -> nodeDao.observeBySeries(scope.seriesId, includeDeleted).map { it.map(NodeEntity::toDomain) }
            is NodeScope.ProjectInSeries -> nodeDao.observeByProjectInSeries(scope.projectId, scope.seriesId, includeDeleted).map { it.map(NodeEntity::toDomain) }
        }

    override suspend fun getTree(scope: NodeScope, rootNodeId: String, includeDeleted: Boolean): NodeTree? {
        val nodes = listByScope(scope, includeDeleted)
        val byId = nodes.associateBy { it.nodeId }
        val childrenByParent = nodes.groupBy { it.parentNodeId }
        val root = byId[rootNodeId] ?: return null

        fun buildTree(node: Node): NodeTree {
            val children = childrenByParent[node.nodeId].orEmpty()
                .sortedBy { it.orderKey }
                .map { buildTree(it) }
            return NodeTree(node, children)
        }

        return buildTree(root)
    }

    override fun observeTree(scope: NodeScope, rootNodeId: String, includeDeleted: Boolean): Flow<NodeTree?> =
        observeByScope(scope, includeDeleted).map { nodes ->
            val byId = nodes.associateBy { it.nodeId }
            val childrenByParent = nodes.groupBy { it.parentNodeId }
            val root = byId[rootNodeId] ?: return@map null

            fun buildTree(node: Node): NodeTree {
                val children = childrenByParent[node.nodeId].orEmpty()
                    .sortedBy { it.orderKey }
                    .map { buildTree(it) }
                return NodeTree(node, children)
            }

            buildTree(root)
        }
}
