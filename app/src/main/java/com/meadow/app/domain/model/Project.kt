package com.meadow.app.domain.model

/**
 * Project.kt
 *
 * Represents a creative project (Book, Movie, Game, etc.)
 * This is the “clean domain model” used in ViewModels and UI,
 * separate from how it’s stored in Room or Firebase.
 */

data class Project(
    val id: String,
    val title: String,
    val promise: String?,
    val pitch: String?,
    val plot: String?,
    val premise: String?,
    val type: String,
    val genres: List<String>,
    val elements: List<String>,
    val audience: String?,
    val rating: String?,
    val warnings: List<String>,
    val status: String,
    val startDate: Long,
    val finishDate: Long?,
    val format: String?,
    val isSeries: Boolean,
    val seriesId: String?
)
