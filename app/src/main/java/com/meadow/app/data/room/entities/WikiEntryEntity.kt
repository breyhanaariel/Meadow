package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wiki_entry_table")
data class WikiEntryEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val content: String,
    val lastModified: Long = System.currentTimeMillis()
)
