package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.WikiDao
import com.meadow.app.data.room.entities.WikiEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WikiRepository.kt
 *
 * Manages wiki pages for a project.
 * Each wiki entry can reference catalog items or other entries.
 */

@Singleton
class WikiRepository @Inject constructor(
    private val dao: WikiDao
) {

    /** Retrieve all wiki pages for a project. */
    fun getWikiByProject(projectId: String): Flow<List<WikiEntryEntity>> =
        dao.getWikiByProject(projectId)

    /** Get one wiki entry. */
    suspend fun getWikiEntry(id: String): WikiEntryEntity? = dao.getWikiEntry(id)

    /** Save or update a wiki entry. */
    suspend fun saveWikiEntry(entry: WikiEntryEntity) = dao.insert(entry)

    /** Delete a wiki entry. */
    suspend fun deleteWikiEntry(entry: WikiEntryEntity) = dao.delete(entry)
}
