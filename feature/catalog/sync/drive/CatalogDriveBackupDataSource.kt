package com.meadow.feature.catalog.sync.drive

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogDriveBackupDataSource @Inject constructor(
    private val repo: CatalogRepositoryContract,
    private val serializer: CatalogDriveSerializer
) {

    suspend fun exportCatalog(catalogId: String): Pair<CatalogItem, String> {
        val catalog = repo.getCatalogById(catalogId)
            ?: throw IllegalArgumentException("Catalog not found")

        return catalog to serializer.serialize(catalog)
    }

    suspend fun exportAllCatalogs(): List<Pair<CatalogItem, String>> =
        repo.getAllCatalogsOnce().map { it to serializer.serialize(it) }

    suspend fun import(json: String) {
        serializer.deserialize(json).forEach {
            repo.saveCatalogItem(it)
        }
    }
}
