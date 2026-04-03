package com.meadow.feature.calendar.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.meadow.feature.calendar.R
import com.meadow.feature.calendar.ui.navigation.CalendarRoutes
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarFeatureSpec @Inject constructor() : ProjectFeatureSpec {

    override val key: String = "calendar"

    override val titleRes: Int = R.string.feature_calendar

    override val icon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }

    override fun routeForProject(projectId: String): String {
        return CalendarRoutes.projectCalendar(projectId)
    }
}
