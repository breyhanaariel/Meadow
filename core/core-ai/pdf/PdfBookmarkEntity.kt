package com.meadow.core.ai.pdf

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_bookmarks")
data class PdfBookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentPath: String,
    val pageNumber: Int,
    val note: String,
    val projectId: String? = null,
    val linkedCatalogId: String? = null
)
