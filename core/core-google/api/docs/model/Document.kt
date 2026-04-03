package com.meadow.core.google.api.docs.model

data class Document(
    val documentId: String?,
    val title: String?,
    val body: DocumentBody?
)

data class DocumentBody(
    val content: List<StructuralElement> = emptyList()
)