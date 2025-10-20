package com.meadow.app.data.firebase

import com.meadow.app.data.repository.SyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * SyncManager.kt
 *
 * High-level orchestrator that coordinates syncing
 * between local (Room) and remote (Firestore, Drive).
 *
 * Triggered manually via “Sync” button or WorkManager task.
 */

class SyncManager(private val syncRepo: SyncRepository) {

    /**
     * Starts a manual sync for all projects.
     */
    fun syncAllProjects() {
        CoroutineScope(Dispatchers.IO).launch {
            syncRepo.syncProjects()
        }
    }

    /**
     * Syncs a single project (when user hits “Sync” on that project).
     */
    fun syncProject(projectId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            syncRepo.syncProject(projectId)
        }
    }
}
