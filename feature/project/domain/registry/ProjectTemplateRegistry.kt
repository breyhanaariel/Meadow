package com.meadow.feature.project.domain.registry

import com.meadow.feature.project.domain.model.ProjectTemplate

interface ProjectTemplateRegistry {
    fun getTemplate(typeKey: String): ProjectTemplate?

    fun getBaseFields(): List<com.meadow.core.data.fields.FieldDefinition>
}