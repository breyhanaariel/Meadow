package com.meadow.feature.script.domain.repository

import com.meadow.feature.script.domain.model.Script
import com.meadow.feature.script.domain.model.ScriptDialect
import com.meadow.feature.script.domain.model.ScriptType
import com.meadow.feature.script.domain.model.ScriptVariant
import kotlinx.coroutines.flow.Flow

interface ScriptRepositoryContract {

    fun observeScriptsByProject(
        projectId: String
    ): Flow<List<Script>>

    suspend fun createScriptForProject(
        projectId: String,
        type: ScriptType,
        dialect: ScriptDialect,
        canonicalLanguage: String
    ): String

    suspend fun getScript(
        scriptId: String
    ): Script?

    fun observeVariants(
        scriptId: String
    ): Flow<List<ScriptVariant>>

    suspend fun getVariant(
        variantId: String
    ): ScriptVariant?

    suspend fun upsertVariantContent(
        variantId: String,
        content: String
    )

    suspend fun ensureCanonicalVariant(
        scriptId: String,
        language: String
    ): String
}
