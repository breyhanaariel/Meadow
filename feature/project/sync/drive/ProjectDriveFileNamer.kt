package com.meadow.feature.project.sync.drive

import com.meadow.feature.project.domain.model.Project

object ProjectDriveFileNamer {

    fun fileName(project: Project): String {
        val title = project.fields
            .firstOrNull { it.definition.id == "title" }
            ?.effectiveRaw()
            ?.takeIf { it.isNotBlank() }
            ?: "Project"

        val safeTitle = title
            .replace(Regex("[^a-zA-Z0-9 _-]"), "")
            .trim()

        return "${safeTitle}_${project.id}.json"
    }
}
