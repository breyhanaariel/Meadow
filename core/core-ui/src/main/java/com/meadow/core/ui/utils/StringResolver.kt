package com.meadow.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun resolveStringId(key: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(
        key,
        "string",
        context.packageName
    )
}
