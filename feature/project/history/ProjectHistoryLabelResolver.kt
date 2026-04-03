package com.meadow.feature.project.history

import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType
import com.meadow.feature.common.api.HistoryLabelResolver
import com.meadow.feature.project.domain.registry.ProjectTemplateRegistry
import javax.inject.Inject

class ProjectHistoryLabelResolver @Inject constructor(
    private val templateRegistry: ProjectTemplateRegistry
) : HistoryLabelResolver {

    override fun supports(ownerType: HistoryOwnerType): Boolean =
        ownerType == HistoryOwnerType.PROJECT

    override fun resolve(entry: HistoryEntry): String {
        val templates = templateRegistry.getAllTemplates()
        val field = templates
            .flatMap { it.fields }
            .firstOrNull { it.id == entry.fieldId || it.key == entry.fieldId }

        return field?.labelKey
            ?: entry.fieldId.substringAfterLast(".")
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }
    }
}