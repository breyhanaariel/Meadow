package com.meadow.app.sync

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.*

/**
 * PresenceManager
 * - Heartbeats every ~15s writing lastActive
 * - Exposes a flow of active collaborators for UI chips/avatars
 */
class PresenceManager(
    private val db: FirebaseFirestore
) {

    data class Presence(
        val displayName: String = "",
        val email: String = "",
        val editingScriptId: String? = null,
        val lastActiveMs: Long = 0L
    )

    fun presenceFlow(projectId: String) = callbackFlow<List<Presence>> {
        val ref = db.collection("projects").document(projectId)
            .collection("presence")
        val reg = ref.addSnapshotListener { snap, err ->
            if (err != null || snap == null) return@addSnapshotListener
            val list = snap.documents.mapNotNull { d ->
                Presence(
                    displayName = d.getString("displayName") ?: "",
                    email = d.getString("email") ?: "",
                    editingScriptId = d.getString("editingScriptId"),
                    lastActiveMs = (d.getTimestamp("lastActive")?.toDate()?.time ?: 0L)
                )
            }
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    suspend fun heartbeat(projectId: String, userId: String, email: String, name: String, editingScriptId: String?) {
        val ref = db.collection("projects").document(projectId)
            .collection("presence").document(userId)

        ref.set(
            mapOf(
                "displayName" to name,
                "email" to email,
                "editingScriptId" to editingScriptId,
                "lastActive" to FieldValue.serverTimestamp()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
    }

    /**
     * Call on screen start; cancels when coroutine scope is cancelled (ViewModel recommended)
     */
    suspend fun runHeartbeatLoop(projectId: String, userId: String, email: String, name: String, editingScriptId: String?) {
        withContext(Dispatchers.IO) {
            while (isActive) {
                heartbeat(projectId, userId, email, name, editingScriptId)
                delay(15_000)
            }
        }
    }
}