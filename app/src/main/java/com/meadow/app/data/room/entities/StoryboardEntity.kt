package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storyboard_frame_table")
data class StoryboardEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val scriptId: String? = null,
    val caption: String? = null,
    val imageUri: String? = null,
    val index: Int = 0
)
