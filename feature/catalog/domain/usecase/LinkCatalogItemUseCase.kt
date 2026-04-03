package com.meadow.feature.catalog.domain.usecase

import com.meadow.feature.catalog.domain.model.CatalogLink
import javax.inject.Inject

class LinkCatalogItemUseCase @Inject constructor() {

    suspend operator fun invoke(
        link: CatalogLink
    ) {
        // Linking persistence will be handled
        // once link storage strategy is finalized
    }
}
