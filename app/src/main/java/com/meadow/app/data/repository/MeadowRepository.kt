package com.meadow.app.data.repository

import com.meadow.app.data.room.dao.*
import com.meadow.app.data.room.entities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeadowRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val scriptDao: ScriptDao,
    private val catalogDao: CatalogDao,
    private val `scriptVersionDao.kt`: `ScriptVersionDao.kt`,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    // Projects
    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAll()
    fun getProject(id: String): Flow<ProjectEntity?> = projectDao.getById(id)
    suspend fun upsertProject(p: ProjectEntity) = projectDao.upsert(p)

    // Scripts
    fun getScriptsForProject(projectId: String): Flow<List<ScriptEntity>> = scriptDao.getForProject(projectId)
    fun getScript(id: String): Flow<ScriptEntity?> = scriptDao.getScript(id)
    suspend fun upsertScript(s: ScriptEntity) {
        scriptDao.upsert(s)
        // snapshot
        `scriptVersionDao.kt`.insert(ScriptVersionEntity(UUID.randomUUID().toString(), s.id, s.content))
    }

    // Catalog
    fun getCatalog(projectId: String?) = catalogDao.getForProject(projectId)
    suspend fun upsertCatalog(item: CatalogItemEntity) = catalogDao.upsert(item)

    // Versions
    fun getVersions(scriptId: String) = `scriptVersionDao.kt`.getVersionsForScript(scriptId)

    // Sync to Firestore
    suspend fun pushProject(project: ProjectEntity) {
        firestore.collection("projects").document(project.id).set(project).await()
    }
    suspend fun pushScript(script: ScriptEntity) {
        firestore.collection("projects").document(script.projectId).collection("scripts").document(script.id).set(script).await()
    }
    suspend fun pushCatalogItem(item: CatalogItemEntity) {
        firestore.collection("catalog").document(item.id).set(item).await()
    }

    // Upload asset to Storage, return URL
    suspend fun uploadAsset(fileName: String, bytes: ByteArray): String {
        val ref = storage.reference.child("assets/${UUID.randomUUID()}_$fileName")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }
}
