package com.meadow.app.sync

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * GeminiPromptSync.kt
 *
 * Keeps Gemini prompt profiles synced between local DataStore and Firebase Firestore.
 */

object GeminiPromptSync {
    private val db = FirebaseFirestore.getInstance()

    suspend fun uploadPrompts(userId: String, prompts: Map<String, String>) {
        try {
            db.collection("gemini_prompts").document(userId).set(prompts).await()
            Log.d("GeminiPromptSync", "Prompts uploaded successfully.")
        } catch (e: Exception) {
            Log.e("GeminiPromptSync", "Upload failed: ${e.message}")
        }
    }

    suspend fun downloadPrompts(userId: String): Map<String, String> {
        return try {
            val doc = db.collection("gemini_prompts").document(userId).get().await()
            doc.data?.mapValues { it.value.toString() } ?: emptyMap()
        } catch (e: Exception) {
            Log.e("GeminiPromptSync", "Download failed: ${e.message}")
            emptyMap()
        }
    }
}