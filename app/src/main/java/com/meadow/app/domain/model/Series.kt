package com.meadow.app.domain.model

/**
 * Series.kt
 *
 * Represents a shared universe or group of related projects.
 * Used for organizing multi-format creative works.
 */

data class Series(
    val id: String,
    val name: String,
    val description: String?,
    val projectIds: List<String>
)
