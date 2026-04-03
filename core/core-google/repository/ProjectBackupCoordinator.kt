package com.meadow.core.google.repository

import com.meadow.core.google.api.drive.DriveFolderManager
import com.meadow.core.google.api.drive.DriveJsonWriter


class ProjectBackupCoordinator(
    private val folderManager: DriveFolderManager,
    private val jsonWriter: DriveJsonWriter
) {

    fun ensureProjectFolders(
        projectId: String
    ): ProjectDrivePaths {
        val meadowRoot =
            folderManager.getOrCreateFolder("Meadow")

        val projectsRoot =
            folderManager.getOrCreateFolder(
                name = "Projects",
                parentId = meadowRoot
            )

        val projectRoot =
            folderManager.getOrCreateFolder(
                name = projectId,
                parentId = projectsRoot
            )

        return ProjectDrivePaths(
            meadowRootId = meadowRoot,
            projectsRootId = projectsRoot,
            projectRootId = projectRoot
        )
    }

    fun ensureFeatureFolder(
        projectRootId: String,
        featureKey: String
    ): String {
        return folderManager.getOrCreateFolder(
            name = featureKey,
            parentId = projectRootId
        )
    }

    fun writeFeatureBackup(
        projectRootId: String,
        featureKey: String,
        fileName: String,
        json: String
    ): String {
        val featureFolder =
            ensureFeatureFolder(projectRootId, featureKey)

        return jsonWriter.writeJson(
            parentFolderId = featureFolder,
            fileName = fileName,
            jsonContent = json
        )

    }
}
