package com.meadow.feature.catalog.domain.usecase

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import javax.inject.Inject

class GetCatalogItemUseCase @Inject constructor(
    private val repository: CatalogRepositoryContract
) {

    suspend operator fun invoke(
        id: String
    ): CatalogItem? =
        repository.getCatalogItemById(id)
}
