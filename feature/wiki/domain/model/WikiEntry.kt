package com.meadow.feature.wiki.domain.model

data class WikiEntry(
    val id: String,
    val projectId: String,
    val title: String,
    val content: String,
    val updatedAt: Long
)
