package com.meadow.feature.common.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun FeatureContextState.BindFeatureContext(
    projectId: String? = null,
    seriesId: String? = null,
    scriptId: String? = null,
    nodeId: String? = null,
    catalogItemId: String? = null
) {
    LaunchedEffect(
        projectId,
        seriesId,
        scriptId,
        nodeId,
        catalogItemId
    ) {
        val current = context.value

        val resolvedSeriesId =
            if (projectId != current.projectId) null else seriesId

        val resolvedScriptId =
            if (seriesId != current.seriesId) null else scriptId

        val resolvedNodeId =
            if (scriptId != current.scriptId) null else nodeId

        val updated = current.copy(
            projectId = projectId,
            seriesId = resolvedSeriesId,
            scriptId = resolvedScriptId,
            // nodeId = resolvedNodeId,
            catalogItemId = catalogItemId
        )

        setContext(updated)
    }
}