package com.meadow.core.node.domain.model

data class NodeTree(
    val node: Node,
    val children: List<NodeTree>
)
