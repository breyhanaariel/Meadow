package com.meadow.core.ai.pdf.reference

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reference_documents",
    indices = [Index(value = ["path"], unique = true)]
)
data class ReferenceDocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val path: String,
    val pageCount: Int,
    val lastModified: Long,
    val indexedAt: Long
)

@Entity(
    tableName = "reference_chunks",
    indices = [
        Index(value = ["documentId"]),
        Index(value = ["pageNumber"])
    ]
)
data class ReferenceChunkEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val documentTitle: String,
    val documentPath: String,
    val pageNumber: Int,
    val text: String
)
