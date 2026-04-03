package com.meadow.core.ai.pdf.reference

data class ReferenceChunk(
    val id: String,
    val documentId: String,
    val documentTitle: String,
    val documentPath: String,
    val pageNumber: Int,
    val text: String
)
