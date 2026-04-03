package com.meadow.feature.script.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "seasons",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["number"]),
        Index(value = ["updatedAt"])
    ]
)
data class SeasonEntity(
    @PrimaryKey
    val seasonId: String = UUID.randomUUID().toString(),

    val projectId: String,
    val number: Int,
    val title: String?,
    val description: String?,
    val arcSummary: String?,
    val status: String,
    val releaseYear: Int?,
    val order: Int,

    val createdAt: Long,
    val updatedAt: Long
)
