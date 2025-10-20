package com.meadow.app.data.repository

import com.meadow.app.data.firebase.FirestoreHelper
import com.meadow.app.data.firebase.StorageHelper
import com.meadow.app.data.firebase.DriveSyncHelper
import com.meadow.app.data.room.dao.ProjectDao
import com.meadow.app.data.room.dao.CatalogDao
import com.meadow.app.data.room.dao.ScriptDao
import com.meadow.app.data.room.entities.ProjectEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SyncRepository.kt
 *
 * Central point for local → cloud synchronization.
 * Combines Firestore + Drive + Room logic.
 */

@Singleton
class SyncRepository @Inject constructor(
    private val firestore: FirestoreHelper,
    private val storage: StorageHelper,
    private val drive: DriveSyncHelper,
    private val projectDao: ProjectDao,
    private val catalogDao: CatalogDao,
    private val scriptDao: ScriptDao
) {

    /** Sync all projects from local → cloud. */
    suspend fun syncProjects() {
        val projects = projectDao.getAllProjects().first()
        projects.forEach { firestore.uploadProject(it) }
    }

    /** Sync a specific project and its catalog/scripts. */
    suspend fun syncProject(projectId: String) {
        val project = projectDao.getProject(projectId) ?: return
        firestore.uploadProject(project)

        // Upload catalog items
        val items = catalogDao.getByProject(projectId).first()
        items.forEach { firestore.uploadCatalogItem(it) }

        // Upload scripts
        val scripts = scriptDao.getByProject(projectId).first()
        scripts.forEach { firestore.uploadScript(it) }
    }

    /** Backup entire project to Google Drive (JSON or ZIP). */
    suspend fun backupToDrive(filePath: String, projectName: String) {
        drive.uploadBackup(filePath, projectName)
    }
}
