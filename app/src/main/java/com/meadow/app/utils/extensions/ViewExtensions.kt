package com.meadow.app.utils.extensions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * ViewExtensions.kt
 *
 * Small helper extensions for Compose layouts.
 */

@Composable
fun VSpacer(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}
