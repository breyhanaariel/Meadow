package com.meadow.feature.script.domain.model

import java.time.Instant

data class Script(
    val scriptId: String,
    val parent: ScriptParent,
    val seasonId: String?,
    val type: ScriptType,
    val dialect: ScriptDialect,
    val canonicalLanguage: LanguageCode,
    val createdAt: Instant,
    val updatedAt: Instant
)
