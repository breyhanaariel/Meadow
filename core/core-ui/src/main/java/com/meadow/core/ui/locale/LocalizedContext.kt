package com.meadow.core.ui.locale

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf

val LocalizedContext = staticCompositionLocalOf<Context> {
    error("LocalizedContext not provided")
}
