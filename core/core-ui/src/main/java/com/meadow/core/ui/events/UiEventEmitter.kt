package com.meadow.core.ui.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface UiEventEmitter {
    val uiEvents: SharedFlow<UiEvent>
}
