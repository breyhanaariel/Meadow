package com.meadow.feature.script.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.script.ui.navigation.registerScriptNavGraph
import javax.inject.Inject

class ScriptFeatureEntryImpl @Inject constructor(
    private val featureContextState: FeatureContextState
) : FeatureEntry {

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.registerScriptNavGraph(
            navController = navController,
            featureContextState = featureContextState
        )
    }
}
