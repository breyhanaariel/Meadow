package com.meadow.core.ai.pdf.reference

import javax.inject.Inject

class ReferenceStore @Inject constructor(
    private val dao: ReferenceDao
) {

    suspend fun upsertDocument(doc: ReferenceDocumentEntity) {
        dao.upsertDocument(doc)
    }

    suspend fun replaceDocumentChunks(
        documentId: String,
        chunks: List<ReferenceChunk>
    ) {
        dao.deleteChunksForDocument(documentId)

        val entities = chunks.map { c ->
            ReferenceChunkEntity(
                id = c.id,
                documentId = c.documentId,
                documentTitle = c.documentTitle,
                documentPath = c.documentPath,
                pageNumber = c.pageNumber,
                text = c.text
            )
        }

        dao.upsertChunks(entities)
    }

    suspend fun getAllChunks(): List<ReferenceChunk> {
        return dao.getAllChunks().map { e ->
            ReferenceChunk(
                id = e.id,
                documentId = e.documentId,
                documentTitle = e.documentTitle,
                documentPath = e.documentPath,
                pageNumber = e.pageNumber,
                text = e.text
            )
        }
    }

    suspend fun getAllDocuments(): List<ReferenceDocumentEntity> =
        dao.getAllDocuments()
}

data class ReferenceHit(
    val chunk: ReferenceChunk,
    val score: Double
)
