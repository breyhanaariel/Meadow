package com.meadow.feature.project.internal.template

import android.content.Context
import com.google.gson.Gson
import com.meadow.core.data.fields.FieldDefinition
import com.meadow.core.data.fields.FieldKind
import com.meadow.feature.project.domain.model.ProjectTemplate
import com.meadow.feature.project.domain.registry.ProjectTemplateRegistry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AssetProjectTemplateRegistry @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val optionsLoader: ProjectOptionsSourceLoader
) : ProjectTemplateRegistry {

    private val templateCache = mutableMapOf<String, ProjectTemplate>()
    private var baseFieldsCache: List<FieldDefinition>? = null

    override fun getTemplate(typeKey: String): ProjectTemplate {
        templateCache[typeKey]?.let { return it }

        val baseFields = loadBaseFields()

        val overrideJson =
            loadJson("${typeKey}_template.json")
                ?: error("Missing template for $typeKey")

        val overrideFields = overrideJson.toFieldDefinitions()
        val merged = mergeFields(baseFields, overrideFields)

        validateFields(typeKey, merged)

        return ProjectTemplate(
            typeKey = typeKey,
            fields = merged
        ).also {
            templateCache[typeKey] = it
        }
    }

    private fun loadBaseFields(): List<FieldDefinition> {
        baseFieldsCache?.let { return it }

        val baseJson =
            loadJson("base_project.json")
                ?: error("Missing base_project.json")

        return baseJson
            .toFieldDefinitions()
            .also { baseFieldsCache = it }
    }

    private fun loadJson(fileName: String): ProjectTemplateJson? =
        runCatching {
            context.assets
                .open("project_templates/$fileName")
                .bufferedReader()
                .use { it.readText() }
                .let { gson.fromJson(it, ProjectTemplateJson::class.java) }
        }.onFailure { e ->
            android.util.Log.e("ProjectTemplate", "Failed to load $fileName", e)
        }.getOrNull()

    private fun ProjectTemplateJson.toFieldDefinitions(): List<FieldDefinition> =
        fields.map { json ->

            val resolvedMetadata =
                resolveMetadata(json.metadata ?: emptyMap())

            FieldDefinition(
                id = json.id,
                owner = "project",
                key = json.key,
                labelKey = json.labelKey,
                descriptionKey = json.descriptionKey,
                kind = FieldKind.valueOf(json.kind),
                isRequired = json.required,
                group = json.group,
                order = json.order,
                metadata = resolvedMetadata
            )
        }

    private fun resolveMetadata(raw: Map<String, Any>): Map<String, Any> {
        if (raw.isEmpty()) return emptyMap()
        val out = raw.toMutableMap()

        // If template references an options source file
        val optionsSource = raw["optionsSource"] as? String
        if (!optionsSource.isNullOrBlank()) {
            out["options"] = optionsLoader.load(optionsSource)
        }

        // If template provides options directly as a list
        val optionsList = raw["options"] as? List<*>
        if (optionsList != null) {
            out["options"] = optionsList.mapNotNull { it?.toString() }.filter { it.isNotBlank() }
        }

        return out
    }

    private fun mergeFields(
        base: List<FieldDefinition>,
        override: List<FieldDefinition>
    ): List<FieldDefinition> =
        (base + override)
            .associateBy { it.id }
            .values
            .sortedBy { it.order }

    private fun validateFields(
        typeKey: String,
        fields: List<FieldDefinition>
    ) {
        val duplicates =
            fields.groupBy { it.id }
                .filter { it.value.size > 1 }
                .keys

        require(duplicates.isEmpty()) {
            "Project template [$typeKey] has duplicate field IDs: $duplicates"
        }
    }

    override fun getBaseFields(): List<FieldDefinition> =
        loadBaseFields()
}
