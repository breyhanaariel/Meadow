package com.meadow.app.domain.usecase

import com.meadow.app.utils.io.ExportUtils
import javax.inject.Inject

/**
 * ExportProjectUseCase.kt
 *
 * Converts a project into a downloadable JSON or ZIP file
 * for local backup or Google Drive export.
 */

class ExportProjectUseCase @Inject constructor(
    private val exportUtils: ExportUtils
) {
    suspend operator fun invoke(projectId: String, format: String): String {
        return exportUtils.exportProject(projectId, format)
    }
}
