package com.meadow.feature.script.ui.state

import com.meadow.feature.script.domain.model.ScriptDialect
import com.meadow.feature.script.domain.model.ScriptType

data class ScriptListUiState(
    val isLoading: Boolean = true,
    val items: List<ScriptListItem> = emptyList()
)

data class ScriptListItem(
    val scriptId: String,
    val type: ScriptType,
    val dialect: ScriptDialect,
    val updatedAt: Long
)
