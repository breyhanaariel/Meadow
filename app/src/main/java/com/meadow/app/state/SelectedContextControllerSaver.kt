package com.meadow.app.state

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import com.meadow.feature.common.api.FeatureContext
import com.meadow.feature.common.state.FeatureContextState

fun SelectedContextControllerSaver(): Saver<FeatureContextState, Any> =
    mapSaver(
        save = { state ->
            val context = state.context.value
            mapOf(
                "projectId" to context.projectId,
                "scriptId" to context.scriptId,
                "catalogItemId" to context.catalogItemId
            )
        },
        restore = { map ->
            FeatureContextState().apply {
                setContext(
                    FeatureContext(
                        projectId = map["projectId"] as String?,
                        scriptId = map["scriptId"] as String?,
                        catalogItemId = map["catalogItemId"] as String?
                    )
                )
            }
        }
    )
