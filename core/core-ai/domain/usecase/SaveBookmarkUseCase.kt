package com.meadow.core.ai.domain.usecase

import com.meadow.core.ai.domain.model.Bookmark
import com.meadow.core.ai.pdf.BookmarkRepository

import javax.inject.Inject

class SaveBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(bookmark: Bookmark): Long =
        bookmarkRepository.saveBookmark(bookmark)
}