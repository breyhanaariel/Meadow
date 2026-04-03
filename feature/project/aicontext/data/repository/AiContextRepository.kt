package com.meadow.feature.project.aicontext.data.repository

import android.content.Context
import com.meadow.feature.project.ui.components.AiFieldHelperScope
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiContextRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun buildContextText(scope: AiFieldHelperScope): String {
        val blocks = mutableListOf<String>()

        scope.projectId?.let {
            blocks.add("PROJECT ID: $it")
        }

        scope.seriesId?.let {
            blocks.add("SERIES ID: $it")
        }

        scope.catalogItemId?.let {
            blocks.add("CATALOG ITEM ID: $it")
        }

        scope.scriptId?.let {
            blocks.add("SCRIPT ID: $it")
        }

        scope.nodeId?.let {
            blocks.add("NODE ID: $it")
        }

        scope.itemTitle
            .takeIf { it.isNotBlank() }
            ?.let {
                blocks.add("ITEM TITLE: $it")
            }

        return if (blocks.isEmpty()) {
            ""
        } else {
            buildString {
                appendLine("FEATURE CONTEXT:")
                blocks.forEach { appendLine(it) }
            }.trim()
        }
    }

    fun readProjectPrompts(): String {
        return readPromptAsset("project_prompts.json")
    }

    fun readSeriesPrompts(): String {
        return readPromptAsset("series_prompts.json")
    }

    private fun readPromptAsset(fileName: String): String {
        return runCatching {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }.getOrDefault("")
    }
}
