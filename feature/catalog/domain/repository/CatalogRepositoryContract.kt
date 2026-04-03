package com.meadow.feature.catalog.domain.repository

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogSyncMeta
import kotlinx.coroutines.flow.Flow

interface CatalogRepositoryContract {

    fun observeAllCatalog(): Flow<List<CatalogItem>>

    fun observeCatalogItem(itemId: String): Flow<CatalogItem?>

    fun observeByProject(projectId: String): Flow<List<CatalogItem>>

    fun observeBySeries(seriesId: String): Flow<List<CatalogItem>>

    fun observeByProjectOrSeries(
        projectId: String,
        seriesId: String?
    ): Flow<List<CatalogItem>>

    suspend fun getCatalogById(id: String): CatalogItem?

    suspend fun getAllCatalogsOnce(): List<CatalogItem>

    suspend fun getCatalogItemById(id: String): CatalogItem? =
        getCatalogById(id)

    suspend fun saveCatalogItem(item: CatalogItem)

    suspend fun deleteCatalogItem(id: String)

    suspend fun getDirtyIds(): List<String>

    suspend fun markSyncFailure(
        ids: List<String>,
        error: String
    )

    suspend fun updateCatalogSyncMeta(
        id: String,
        syncMeta: CatalogSyncMeta
    )
    suspend fun replaceAllCatalogItems(items: List<CatalogItem>)

    fun observeCountForProject(projectId: String): Flow<Int>
}
