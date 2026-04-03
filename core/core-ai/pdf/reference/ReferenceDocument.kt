package com.meadow.core.ai.pdf.reference

data class ReferenceDocument(
    val id: String,
    val title: String,
    val path: String,
    val pageCount: Int,
    val lastModified: Long
)
