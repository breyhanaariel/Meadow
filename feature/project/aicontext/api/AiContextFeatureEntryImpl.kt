package com.meadow.feature.project.aicontext.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry
import javax.inject.Inject

class AiContextFeatureEntryImpl @Inject constructor(
    private val featureContextState: FeatureContextState
): FeatureEntry {

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        // navGraphBuilder.registerCalendarNavGraph(
           // navController = navController,
            // featureContextState = featureContextState
       // )
    }
}
