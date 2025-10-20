package com.meadow.app.domain.usecase

import com.meadow.app.data.repository.CatalogRepository
import com.meadow.app.data.room.entities.CatalogItemEntity
import javax.inject.Inject

/**
 * LinkCatalogItemUseCase.kt
 *
 * Allows one catalog item to reference another
 * (e.g., link Character → Location or Prop → Scene).
 */

class LinkCatalogItemUseCase @Inject constructor(
    private val repo: CatalogRepository
) {
    suspend operator fun invoke(
        item: CatalogItemEntity,
        linkId: String
    ): CatalogItemEntity {
        val updated = item.copy(
            linkedItems = item.linkedItems + linkId
        )
        repo.saveItem(updated)
        return updated
    }
}
