package com.meadow.feature.catalog.domain.usecase

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import javax.inject.Inject

class DeleteCatalogItemUseCase @Inject constructor(
    private val repository: CatalogRepositoryContract
) {
    suspend operator fun invoke(id: String) {
        repository.deleteCatalogItem(id)
    }
}
