package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.FamilyTreeDao
import com.meadow.app.data.room.entities.FamilyLinkEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FamilyTreeRepository.kt
 *
 * Manages character relationships within a project.
 */

@Singleton
class FamilyTreeRepository @Inject constructor(
    private val dao: FamilyTreeDao
) {

    /** Get all relationships for a project. */
    fun getLinks(projectId: String): Flow<List<FamilyLinkEntity>> = dao.getLinks(projectId)

    /** Save or update a relationship. */
    suspend fun saveLink(link: FamilyLinkEntity) = dao.insert(link)

    /** Delete a relationship. */
    suspend fun deleteLink(link: FamilyLinkEntity) = dao.delete(link)
}
