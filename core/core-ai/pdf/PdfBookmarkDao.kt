package com.meadow.core.ai.pdf

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfBookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: PdfBookmarkEntity): Long

    @Query("SELECT * FROM pdf_bookmarks WHERE documentPath = :documentPath")
    fun getBookmarksForDocument(documentPath: String): Flow<List<PdfBookmarkEntity>>

    @Query("SELECT * FROM pdf_bookmarks")
    fun getAllBookmarks(): Flow<List<PdfBookmarkEntity>>


    @Query(
        """
        SELECT * FROM pdf_bookmarks
        WHERE projectId = :projectId
    """
    )
    fun getBookmarksForProject(projectId: String): Flow<List<PdfBookmarkEntity>>

}
