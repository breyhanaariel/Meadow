package com.meadow.feature.script.domain.model

import java.time.Instant

data class Season(
    val seasonId: String,
    val projectId: String,
    val number: Int,
    val title: String?,
    val description: String?,
    val arcSummary: String?,
    val status: SeasonStatus,
    val releaseYear: Int?,
    val order: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)
