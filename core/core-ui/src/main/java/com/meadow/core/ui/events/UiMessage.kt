package com.meadow.core.ui.events

import androidx.annotation.StringRes

sealed interface UiMessage {
    data class Snackbar(
        @StringRes val messageRes: Int
    ) : UiMessage
}
