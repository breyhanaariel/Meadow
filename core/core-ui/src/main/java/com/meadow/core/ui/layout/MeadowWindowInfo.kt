package com.meadow.core.ui.layout

import android.app.Activity
import androidx.compose.material3.windowsizeclass.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

enum class MeadowWidthClass {
    Phone,
    Tablet
}

data class MeadowWindowInfo(
    val widthClass: MeadowWidthClass,
    val isLandscape: Boolean
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberMeadowWindowInfo(): MeadowWindowInfo {
    val activity = LocalContext.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)

    val widthClass = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> MeadowWidthClass.Phone
        else -> MeadowWidthClass.Tablet
    }

    return remember(windowSizeClass) {
        MeadowWindowInfo(
            widthClass = widthClass,
            isLandscape = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
        )
    }
}