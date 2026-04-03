package com.meadow.core.ui.events

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.meadow.core.ui.locale.LocalizedContext
import kotlinx.coroutines.flow.Flow

@Composable
fun CollectUiMessages(
    messages: Flow<UiMessage>,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val localized = LocalizedContext.current

    LaunchedEffect(messages) {
        messages.collect { message ->
            when (message) {
                is UiMessage.Snackbar -> {
                    snackbarHostState.showSnackbar(
                        localized.getString(message.messageRes)
                    )
                }
            }
        }
    }
}
