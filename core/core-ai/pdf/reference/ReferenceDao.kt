package com.meadow.core.ai.pdf.reference

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDocument(doc: ReferenceDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChunks(chunks: List<ReferenceChunkEntity>)

    @Query("SELECT * FROM reference_chunks")
    suspend fun getAllChunks(): List<ReferenceChunkEntity>

    @Query("SELECT * FROM reference_documents")
    suspend fun getAllDocuments(): List<ReferenceDocumentEntity>

    @Query("DELETE FROM reference_chunks WHERE documentId = :documentId")
    suspend fun deleteChunksForDocument(documentId: String)

    @Query("DELETE FROM reference_documents WHERE id = :documentId")
    suspend fun deleteDocument(documentId: String)

    @Query(
        """
        SELECT * FROM reference_chunks
        WHERE text LIKE '%' || :term || '%'
        LIMIT :limit
        """
    )
    suspend fun searchChunksLike(term: String, limit: Int): List<ReferenceChunkEntity>
}
