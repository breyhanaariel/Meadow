package com.meadow.feature.calendar.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface CalendarFeatureHomeProvider {

    val featureKey: String
        get() = "calendar"

    @Composable
    fun HomeContentOrNull(
        navController: NavHostController
    )
}
