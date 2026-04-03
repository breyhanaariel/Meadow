package com.meadow.feature.calendar.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.calendar.ui.navigation.registerCalendarNavGraph
import javax.inject.Inject

class CalendarFeatureEntryImpl @Inject constructor(
    private val featureContextState: FeatureContextState
) : FeatureEntry {

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.registerCalendarNavGraph(
            navController = navController,
            featureContextState = featureContextState
        )
    }
}
