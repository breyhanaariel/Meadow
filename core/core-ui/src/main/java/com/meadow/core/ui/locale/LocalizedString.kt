package com.meadow.core.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun localizedString(id: Int): String {
    val context = LocalizedContext.current
    return context.getString(id)
}
