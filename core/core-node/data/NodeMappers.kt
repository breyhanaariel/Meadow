package com.meadow.core.node.data

import com.meadow.core.node.domain.model.Node
import com.meadow.core.node.domain.model.NodeEdge
import com.meadow.core.node.domain.model.NodeEdgeSyncMeta
import com.meadow.core.node.domain.model.NodeScope
import com.meadow.core.node.domain.model.NodeSyncMeta

internal fun NodeEntity.toDomain(): Node {
    val scope = when {
        projectId != null && seriesId != null -> NodeScope.ProjectInSeries(projectId, seriesId)
        projectId != null -> NodeScope.Project(projectId)
        seriesId != null -> NodeScope.Series(seriesId)
        else -> NodeScope.Project("")
    }
    return Node(
        nodeId = nodeId,
        scope = scope,
        parentNodeId = parentNodeId,
        type = type,
        title = title,
        content = content,
        orderKey = orderKey,
        metadataJson = metadataJson,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        syncMeta = NodeSyncMeta(
            localVersion = localVersion,
            remoteVersion = remoteVersion,
            isDirty = isDirty,
            lastSyncAt = lastSyncAt
        )
    )
}

internal fun Node.toEntity(): NodeEntity {
    val projectId = when (scope) {
        is NodeScope.Project -> scope.projectId
        is NodeScope.ProjectInSeries -> scope.projectId
        is NodeScope.Series -> null
    }
    val seriesId = when (scope) {
        is NodeScope.Series -> scope.seriesId
        is NodeScope.ProjectInSeries -> scope.seriesId
        is NodeScope.Project -> null
    }
    return NodeEntity(
        nodeId = nodeId,
        projectId = projectId,
        seriesId = seriesId,
        parentNodeId = parentNodeId,
        type = type,
        title = title,
        content = content,
        orderKey = orderKey,
        metadataJson = metadataJson,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        localVersion = syncMeta.localVersion,
        remoteVersion = syncMeta.remoteVersion,
        isDirty = syncMeta.isDirty,
        lastSyncAt = syncMeta.lastSyncAt
    )
}

internal fun NodeEdgeEntity.toDomain(): NodeEdge =
    NodeEdge(
        edgeId = edgeId,
        fromNodeId = fromNodeId,
        toNodeId = toNodeId,
        type = type,
        metadataJson = metadataJson,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        syncMeta = NodeEdgeSyncMeta(
            localVersion = localVersion,
            remoteVersion = remoteVersion,
            isDirty = isDirty,
            lastSyncAt = lastSyncAt
        )
    )

internal fun NodeEdge.toEntity(): NodeEdgeEntity =
    NodeEdgeEntity(
        edgeId = edgeId,
        fromNodeId = fromNodeId,
        toNodeId = toNodeId,
        type = type,
        metadataJson = metadataJson,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        localVersion = syncMeta.localVersion,
        remoteVersion = syncMeta.remoteVersion,
        isDirty = syncMeta.isDirty,
        lastSyncAt = syncMeta.lastSyncAt
    )
