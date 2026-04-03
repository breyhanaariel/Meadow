package com.meadow.feature.catalog.domain.usecase

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import javax.inject.Inject

class CreateCatalogItemUseCase @Inject constructor(
    private val repository: CatalogRepositoryContract
) {

    suspend operator fun invoke(
        item: CatalogItem
    ) {
        repository.saveCatalogItem(item)
    }
}
