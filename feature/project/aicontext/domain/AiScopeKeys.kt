package com.meadow.feature.project.aicontext.domain

object AiScopeKeys {
    const val GLOBAL = "global"

    fun project(projectId: String) =
        "project:$projectId"

    fun series(seriesId: String) =
        "series:$seriesId"

    fun catalog(projectId: String, catalogId: String) =
        "project:$projectId|catalog:$catalogId"

    fun script(projectId: String, scriptId: String) =
        "project:$projectId|script:$scriptId"

    fun node(projectId: String, nodeId: String) =
        "project:$projectId|node:$nodeId"
}
