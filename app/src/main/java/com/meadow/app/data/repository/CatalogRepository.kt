package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.CatalogDao
import com.meadow.app.data.room.entities.CatalogItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CatalogRepository.kt
 *
 * Handles catalog data for a project (Characters, Props, Worlds, etc.)
 * Provides filtered access and cross-link logic.
 */

@Singleton
class CatalogRepository @Inject constructor(
    private val dao: CatalogDao
) {

    /** Gets all catalog items for a given project. */
    fun getCatalogForProject(projectId: String): Flow<List<CatalogItemEntity>> =
        dao.getByProject(projectId)

    /** Gets a specific catalog item. */
    suspend fun getItem(id: String): CatalogItemEntity? = dao.getItem(id)

    /** Inserts or updates an item. */
    suspend fun saveItem(item: CatalogItemEntity) = dao.insert(item)

    /** Deletes an item. */
    suspend fun deleteItem(item: CatalogItemEntity) = dao.delete(item)

    /** Filters items by type (e.g., "Character" or "Location"). */
    fun filterByType(items: List<CatalogItemEntity>, type: String): List<CatalogItemEntity> {
        return items.filter { it.type == type }
    }
}
