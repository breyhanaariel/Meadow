package com.meadow.feature.familytree.domain.model

data class FamilyLink(
    val id: String,
    val projectId: String,
    val parentId: String?,
    val childId: String?,
    val relation: String
)
