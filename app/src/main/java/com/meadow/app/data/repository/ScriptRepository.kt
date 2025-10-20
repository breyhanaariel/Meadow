package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.ScriptDao
import com.meadow.app.data.room.entities.ScriptEntity
import com.meadow.app.data.room.entities.ScriptVersionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ScriptRepository.kt
 *
 * Handles local script storage, versioning, and autosave logic.
 */

@Singleton
class ScriptRepository @Inject constructor(
    private val dao: ScriptDao
) {

    /** Get all scripts for a project. */
    fun getScripts(projectId: String): Flow<List<ScriptEntity>> = dao.getByProject(projectId)

    /** Save or update a script. */
    suspend fun saveScript(script: ScriptEntity) = dao.insert(script)

    /** Save a version snapshot (autosave or manual). */
    suspend fun saveVersion(scriptId: String, content: String) {
        val version = ScriptVersionEntity(scriptId = scriptId, content = content)
        dao.insertVersion(version)
    }

    /** Retrieve all versions for a given script. */
    fun getVersions(scriptId: String): Flow<List<ScriptVersionEntity>> =
        dao.getVersions(scriptId)
}
