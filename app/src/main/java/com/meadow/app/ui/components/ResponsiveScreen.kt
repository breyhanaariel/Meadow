package com.meadow.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ResponsiveScreen(
    windowType: WindowWidthSizeClass,
    phoneContent: @Composable ColumnScope.() -> Unit,
    tabletContent: @Composable RowScope.() -> Unit
) {
    when (windowType) {
        WindowWidthSizeClass.Compact -> Column(Modifier.fillMaxSize(), content = phoneContent)
        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded -> Row(Modifier.fillMaxSize(), content = tabletContent)
        else -> Column(Modifier.fillMaxSize(), content = phoneContent)
    }
}
