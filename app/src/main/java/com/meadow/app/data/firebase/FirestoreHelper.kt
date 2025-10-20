package com.meadow.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.meadow.app.data.room.entities.*
import kotlinx.coroutines.tasks.await

/**
 * FirestoreHelper.kt
 *
 * Wraps Firebase Firestore operations for Meadow.
 * Responsible for syncing projects, catalog items, and scripts.
 *
 * This allows offline-first behavior — local Room database first,
 * and Firestore updates triggered manually or via WorkManager.
 */

class FirestoreHelper(private val firestore: FirebaseFirestore) {

    // --- PROJECTS COLLECTION ---
    suspend fun uploadProject(project: ProjectEntity) {
        firestore.collection("projects")
            .document(project.id)
            .set(project, SetOptions.merge())
            .await()
    }

    suspend fun getProject(id: String): ProjectEntity? {
        val snapshot = firestore.collection("projects")
            .document(id)
            .get()
            .await()
        return snapshot.toObject(ProjectEntity::class.java)
    }

    // --- CATALOG COLLECTION ---
    suspend fun uploadCatalogItem(item: CatalogItemEntity) {
        firestore.collection("catalog_items")
            .document(item.id)
            .set(item, SetOptions.merge())
            .await()
    }

    suspend fun getCatalogItems(projectId: String): List<CatalogItemEntity> {
        val snapshot = firestore.collection("catalog_items")
            .whereEqualTo("projectId", projectId)
            .get()
            .await()
        return snapshot.toObjects(CatalogItemEntity::class.java)
    }

    // --- SCRIPT COLLECTION ---
    suspend fun uploadScript(script: ScriptEntity) {
        firestore.collection("scripts")
            .document(script.id)
            .set(script, SetOptions.merge())
            .await()
    }

    suspend fun getScripts(projectId: String): List<ScriptEntity> {
        val snapshot = firestore.collection("scripts")
            .whereEqualTo("projectId", projectId)
            .get()
            .await()
        return snapshot.toObjects(ScriptEntity::class.java)
    }
}
