package com.meadow.feature.script.domain.model

sealed class ScriptParent {
    data class Project(
        val projectId: String
    ) : ScriptParent()

    data class Series(
        val seriesId: String
    ) : ScriptParent()

    data class ProjectInSeries(
        val projectId: String,
        val seriesId: String
    ) : ScriptParent()
}
