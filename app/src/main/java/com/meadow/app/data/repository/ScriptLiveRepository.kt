package com.meadow.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * ScriptLiveRepository
 * - Simple live editing using a single Firestore doc per script (start here).
 * - Provides a stream of updates and a safe "versioned" update to avoid clobbers.
 *
 * For heavy teams: move to chunked storage (script_chunks) to reduce conflicts.
 */
class ScriptLiveRepository(
    private val db: FirebaseFirestore
) {

    data class ScriptDoc(
        val content: String = "",
        val version: Long = 0,
        val updatedBy: String = "",
        val updatedAt: Timestamp? = null
    )

    fun observeScript(projectId: String, scriptId: String) = callbackFlow<ScriptDoc> {
        val ref = db.collection("projects")
            .document(projectId)
            .collection("scripts")
            .document(scriptId)

        val reg = ref.addSnapshotListener { snap, err ->
            if (err != null || snap == null || !snap.exists()) return@addSnapshotListener
            trySend(
                ScriptDoc(
                    content = snap.getString("content") ?: "",
                    version = snap.getLong("version") ?: 0,
                    updatedBy = snap.getString("updatedBy") ?: "",
                    updatedAt = snap.getTimestamp("updatedAt")
                )
            )
        }
        awaitClose { reg.remove() }
    }

    /**
     * Version-checked "last-write-wins (soft)" update:
     * - Reads current version, compares with callerKnownVersion
     * - If local is older, writes new content with version+1
     * - If remote moved ahead, returns false for caller to retry (pull/merge/apply)
     */
    suspend fun tryUpdateScript(
        projectId: String,
        scriptId: String,
        newContent: String,
        callerKnownVersion: Long,
        userEmail: String
    ): Boolean {
        val ref = db.collection("projects")
            .document(projectId)
            .collection("scripts")
            .document(scriptId)

        return db.runTransaction { tx ->
            val snap = tx.get(ref)
            val curVersion = snap.getLong("version") ?: 0
            if (curVersion > callerKnownVersion) {
                // Remote has advanced; bail to let caller merge.
                false
            } else {
                tx.update(ref, mapOf(
                    "content" to newContent,
                    "version" to (curVersion + 1),
                    "updatedBy" to userEmail,
                    "updatedAt" to FieldValue.serverTimestamp(),
                ))
                true
            }
        }.await()
    }
}