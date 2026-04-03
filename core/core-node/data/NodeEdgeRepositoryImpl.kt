package com.meadow.core.node.data

import com.meadow.core.node.domain.model.NodeEdge
import com.meadow.core.node.repository.NodeEdgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NodeEdgeRepositoryImpl @Inject constructor(
    private val edgeDao: NodeEdgeDao
) : NodeEdgeRepository {

    override suspend fun upsert(edge: NodeEdge) {
        edgeDao.upsert(edge.toEntity())
    }

    override suspend fun softDelete(edgeId: String, updatedAt: Long) {
        edgeDao.softDelete(edgeId, updatedAt)
    }

    override suspend fun get(edgeId: String): NodeEdge? =
        edgeDao.get(edgeId)?.toDomain()

    override fun observe(edgeId: String): Flow<NodeEdge?> =
        edgeDao.observe(edgeId).map { it?.toDomain() }

    override suspend fun listByNode(nodeId: String, includeDeleted: Boolean): List<NodeEdge> =
        edgeDao.listByNode(nodeId, includeDeleted).map { it.toDomain() }

    override fun observeByNode(nodeId: String, includeDeleted: Boolean): Flow<List<NodeEdge>> =
        edgeDao.observeByNode(nodeId, includeDeleted).map { it.map(NodeEdgeEntity::toDomain) }
}
