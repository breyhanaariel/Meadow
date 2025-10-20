package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * One creative project (standalone or part of a series).
 * The rich fields match your latest spec but are kept flexible via JSON blobs.
 */
@Entity(tableName = "project_table")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,                 // Movie, TV Show, Novel, Comic, Game, Play, Anime
    val seriesId: String? = null,     // null if standalone
    val status: String? = null,       // Idea, Outline, Draft, Production, Released
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    // Large/variable fields stored as JSON for forward-compat:
    val promise: String? = null,
    val pitch: String? = null,
    val plot: String? = null,
    val premise: String? = null,
    val genresJson: String? = null,     // JSON array of enums
    val elementsJson: String? = null,   // JSON array of enums
    val audience: String? = null,
    val ratingsJson: String? = null,    // JSON array of enums
    val warningsJson: String? = null,   // JSON array of enums
    val format: String? = null,         // Prequel, Serial, etc.
    val startDate: Long? = null,
    val finishDate: Long? = null
)


