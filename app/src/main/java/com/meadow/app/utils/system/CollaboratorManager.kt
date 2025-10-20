package com.meadow.app.utils.system

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * CollaboratorManager.kt
 *
 * Handles inviting collaborators, assigning roles, and updating shared project access
 * via Firebase Firestore. Each project can have multiple contributors.
 */

object CollaboratorManager {

    private val db = FirebaseFirestore.getInstance()

    data class Collaborator(
        val name: String = "",
        val email: String = "",
        val role: String = "Editor", // Owner, Editor, Viewer
        val projectId: String = ""
    )

    suspend fun inviteCollaborator(projectId: String, name: String, email: String): Boolean {
        return try {
            val collaborator = Collaborator(
                name = name,
                email = email,
                role = "Editor",
                projectId = projectId
            )
            db.collection("collaborators").add(collaborator).await()
            true
        } catch (e: Exception) {
            Log.e("CollaboratorManager", "Invite failed: ${e.message}")
            false
        }
    }

    suspend fun getCollaborators(projectId: String): List<Collaborator> {
        return try {
            val snapshot = db.collection("collaborators")
                .whereEqualTo("projectId", projectId)
                .get()
                .await()
            snapshot.toObjects(Collaborator::class.java)
        } catch (e: Exception) {
            Log.e("CollaboratorManager", "Error fetching collaborators: ${e.message}")
            emptyList()
        }
    }

    suspend fun removeCollaborator(projectId: String, email: String): Boolean {
        return try {
            val snapshot = db.collection("collaborators")
                .whereEqualTo("projectId", projectId)
                .whereEqualTo("email", email)
                .get()
                .await()

            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
            true
        } catch (e: Exception) {
            Log.e("CollaboratorManager", "Error removing collaborator: ${e.message}")
            false
        }
    }

    suspend fun updateCollaboratorRole(projectId: String, email: String, role: String): Boolean {
        return try {
            val snapshot = db.collection("collaborators")
                .whereEqualTo("projectId", projectId)
                .whereEqualTo("email", email)
                .get()
                .await()

            for (doc in snapshot.documents) {
                doc.reference.update("role", role).await()
            }
            true
        } catch (e: Exception) {
            Log.e("CollaboratorManager", "Error updating role: ${e.message}")
            false
        }
    }
}