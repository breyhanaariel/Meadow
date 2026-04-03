package com.meadow.feature.project.data.local


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "series",
    indices = [Index("title")]
)
data class SeriesEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val projectIds: String = "",
    val sharedFieldIds: String,
    val sharedCatalogFieldIds: String = ""

)

