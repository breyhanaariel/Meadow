package com.meadow.core.ai.domain.model

data class AiContextPayload(
    val summary: String,
    val tone: String,
    val themes: List<String>,
    val projectId: String? = null,
    val seriesId: String? = null,
    val scopeKey: String? = null

) {
    fun isEmpty(): Boolean =
        summary.isBlank() && tone.isBlank() && themes.isEmpty()
}
