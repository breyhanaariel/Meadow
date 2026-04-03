package com.meadow.feature.project.sync.drive

import com.meadow.core.google.api.drive.internal.DriveBackupRepository
import com.meadow.core.google.api.drive.internal.DriveUploadResult
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectDriveBackupCoordinator @Inject constructor(
    private val driveRepository: DriveBackupRepository,
    private val projectRepo: ProjectRepositoryContract,
    private val serializer: ProjectDriveSerializer
) {

    private val now get() = System.currentTimeMillis()

    private fun projectAppProps(projectId: String): Map<String, String> =
        mapOf("projectId" to projectId)

    suspend fun backupProject(projectId: String) {
        val project = projectRepo.getProjectById(projectId)
            ?: return

        val json = serializer.serialize(project)
        val fileName = ProjectDriveFileNamer.fileName(project)

        val result = driveRepository.backupFile(
            fileName = fileName,
            json = json,
            appProperties = projectAppProps(project.id)
        )

        if (result is DriveUploadResult.Success) {
            projectRepo.updateProjectSyncMeta(
                projectId,
                project.syncMeta.copy(
                    lastDriveBackupAt = now,
                    lastSyncError = null
                )
            )
            return
        }

        if (result is DriveUploadResult.Failure) {
            projectRepo.updateProjectSyncMeta(
                projectId,
                project.syncMeta.copy(
                    lastSyncError = result.reason
                )
            )
            throw result.throwable ?: RuntimeException(result.reason)
        }
    }

    suspend fun backupAllNow() {
        val projects = projectRepo.getAllProjectsOnce()

        projects.forEach { project ->
            val json = serializer.serialize(project)
            val fileName = ProjectDriveFileNamer.fileName(project)

            val result = driveRepository.backupFile(
                fileName = fileName,
                json = json,
                appProperties = projectAppProps(project.id)
            )

            if (result is DriveUploadResult.Success) {
                projectRepo.updateProjectSyncMeta(
                    project.id,
                    project.syncMeta.copy(
                        lastDriveBackupAt = now,
                        lastSyncError = null
                    )
                )
            } else if (result is DriveUploadResult.Failure) {
                projectRepo.updateProjectSyncMeta(
                    project.id,
                    project.syncMeta.copy(
                        lastSyncError = result.reason
                    )
                )
            }
        }
    }
}
