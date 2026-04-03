package com.meadow.core.node.domain.model

sealed class NodeScope {
    data class Project(val projectId: String) : NodeScope()
    data class Series(val seriesId: String) : NodeScope()
    data class ProjectInSeries(val projectId: String, val seriesId: String) : NodeScope()
}

fun NodeScope.projectIdOrNull(): String? = when (this) {
    is NodeScope.Project -> projectId
    is NodeScope.ProjectInSeries -> projectId
    is NodeScope.Series -> null
}

fun NodeScope.seriesIdOrNull(): String? = when (this) {
    is NodeScope.Series -> seriesId
    is NodeScope.ProjectInSeries -> seriesId
    is NodeScope.Project -> null
}
