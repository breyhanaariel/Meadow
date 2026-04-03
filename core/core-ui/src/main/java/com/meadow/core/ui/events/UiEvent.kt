package com.meadow.core.ui.events

sealed interface UiEvent {
    data class Snackbar(
        val messageRes: Int,
        val actionRes: Int? = null
    ) : UiEvent
}
