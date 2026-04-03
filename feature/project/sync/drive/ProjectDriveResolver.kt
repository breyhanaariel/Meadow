package com.meadow.feature.project.sync.drive

import com.meadow.core.google.api.drive.internal.DriveFolderResolver
import jakarta.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectDriveResolver @Inject constructor(
    private val folderResolver: DriveFolderResolver
) {

    companion object {
        private const val ROOT_FOLDER = "Meadow"
        private const val PROJECTS_FOLDER = "Projects"
    }

    private var cachedProjectsFolderId: String? = null

    suspend fun getOrCreateProjectsFolder(): String {
        if (cachedProjectsFolderId != null) {
            return cachedProjectsFolderId!!
        }

        val meadowFolderId =
            folderResolver.getOrCreateFolder(ROOT_FOLDER)

        val projectsFolderId =
            folderResolver.getOrCreateFolder(
                PROJECTS_FOLDER,
                meadowFolderId
            )

        cachedProjectsFolderId = projectsFolderId
        return projectsFolderId
    }
}
