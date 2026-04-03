package com.meadow.feature.catalog.data.repository

import com.meadow.feature.catalog.data.local.CatalogFieldValueDao
import com.meadow.feature.catalog.data.local.CatalogItemDao
import com.meadow.feature.catalog.data.mappers.CatalogItemMapper
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogSyncMeta
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import com.meadow.feature.catalog.internal.schema.CatalogSchemaRegistry
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class CatalogRepository @Inject constructor(
    private val itemDao: CatalogItemDao,
    private val fieldValueDao: CatalogFieldValueDao,
    private val schemaRegistry: CatalogSchemaRegistry
) : CatalogRepositoryContract {

    override fun observeAllCatalog(): Flow<List<CatalogItem>> =
        itemDao.observeAllCatalog().map { it.map(CatalogItemMapper::toDomainLite) }

    override fun observeCatalogItem(itemId: String): Flow<CatalogItem?> =
        combine(
            itemDao.observeById(itemId),
            fieldValueDao.observeForItem(itemId)
        ) { entity, values ->
            if (entity == null) return@combine null
            val schema = schemaRegistry.getSchema(entity.schemaId) ?: return@combine null
            CatalogItemMapper.toDomainFull(entity, values, schema)
        }

    override fun observeByProject(projectId: String): Flow<List<CatalogItem>> =
        itemDao.observeByProject(projectId).map { it.map(CatalogItemMapper::toDomainLite) }

    override fun observeBySeries(seriesId: String): Flow<List<CatalogItem>> =
        itemDao.observeBySeries(seriesId).map { it.map(CatalogItemMapper::toDomainLite) }

    override fun observeByProjectOrSeries(
        projectId: String,
        seriesId: String?
    ): Flow<List<CatalogItem>> =
        if (seriesId != null) observeBySeries(seriesId) else observeByProject(projectId)

    override suspend fun getCatalogById(id: String): CatalogItem? {
        val entity = itemDao.getCatalogItemById(id) ?: return null
        val schema = schemaRegistry.getSchema(entity.schemaId) ?: return null
        val values = fieldValueDao.getForItem(entity.id)
        return CatalogItemMapper.toDomainFull(entity, values, schema)
    }

    override suspend fun getAllCatalogsOnce(): List<CatalogItem> =
        itemDao.getAllCatalogOnce().mapNotNull {
            val schema = schemaRegistry.getSchema(it.schemaId) ?: return@mapNotNull null
            val values = fieldValueDao.getForItem(it.id)
            CatalogItemMapper.toDomainFull(it, values, schema)
        }

    override suspend fun saveCatalogItem(item: CatalogItem) {
        val schema = schemaRegistry.getSchema(item.schemaId)
            ?: error("Missing schema ${item.schemaId}")

        itemDao.upsert(CatalogItemMapper.toEntity(item, schema))
        fieldValueDao.deleteForItem(item.id)
        fieldValueDao.upsertAll(CatalogItemMapper.toFieldEntities(item))
    }

    override suspend fun deleteCatalogItem(id: String) {
        fieldValueDao.deleteForItem(id)
        itemDao.getCatalogItemById(id)?.let { itemDao.deleteCatalogItem(it) }
    }

    override suspend fun getDirtyIds(): List<String> =
        itemDao.getDirtyItemIds()

    override suspend fun markSyncFailure(ids: List<String>, error: String) {
        itemDao.markSyncFailure(ids, error)
    }

    override suspend fun updateCatalogSyncMeta(id: String, syncMeta: CatalogSyncMeta) {
        // intentionally no-op: CatalogItem domain does not carry syncMeta
    }

    override suspend fun replaceAllCatalogItems(items: List<CatalogItem>) {
        itemDao.clearAll()
        fieldValueDao.clearAll()

        items.forEach { item ->
            val schema = schemaRegistry.getSchema(item.schemaId)
                ?: return@forEach

            itemDao.upsert(CatalogItemMapper.toEntity(item, schema))
            fieldValueDao.upsertAll(CatalogItemMapper.toFieldEntities(item))
        }
    }

    override fun observeCountForProject(projectId: String): Flow<Int> =
        itemDao.observeCountForProject(projectId)
}
