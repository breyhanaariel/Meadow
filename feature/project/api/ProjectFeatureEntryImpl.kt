package com.meadow.feature.project.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.project.ui.navigation.registerProjectNavGraph
import javax.inject.Inject

class ProjectFeatureEntryImpl @Inject constructor(
    private val featureContextState: FeatureContextState
) : FeatureEntry {

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.registerProjectNavGraph(
            navController = navController,
            featureContextState = featureContextState
        )
    }
}
