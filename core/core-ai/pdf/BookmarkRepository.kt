package com.meadow.core.ai.pdf

import com.meadow.core.ai.domain.model.Bookmark
import com.meadow.core.ai.pdf.PdfBookmarkDao
import com.meadow.core.ai.pdf.PdfBookmarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarkRepository(
    private val dao: PdfBookmarkDao
) {

    suspend fun saveBookmark(bookmark: Bookmark): Long {
        val entity = PdfBookmarkEntity(
            id = bookmark.id ?: 0,
            documentPath = bookmark.documentPath,
            pageNumber = bookmark.pageNumber,
            note = bookmark.note ?: "",
            linkedCatalogId = bookmark.linkedCatalogId,
            projectId = bookmark.projectId
        )

        return dao.insert(entity)
    }



    fun getBookmarksForDocument(documentPath: String): Flow<List<Bookmark>> =
        dao.getBookmarksForDocument(documentPath).map { entities ->
            entities.map { it.toDomain() }
        }

    fun getBookmarksForProject(projectId: String): Flow<List<Bookmark>> =
        dao.getBookmarksForProject(projectId).map { list ->
            list.map { it.toDomain() }
        }

    fun getAllBookmarks(): Flow<List<Bookmark>> =
        dao.getAllBookmarks().map { entities ->
            entities.map { it.toDomain() }
        }

    private fun PdfBookmarkEntity.toDomain() = Bookmark(
        id = id,
        documentPath = documentPath,
        pageNumber = pageNumber,
        note = note,
        linkedCatalogId = linkedCatalogId,
        projectId = projectId
    )

}