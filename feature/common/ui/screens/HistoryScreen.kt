package com.meadow.feature.common.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meadow.feature.common.state.HistoryEntryUi
import com.meadow.feature.common.state.HistorySessionUi
import com.meadow.feature.common.ui.viewmodel.HistoryViewModel
import kotlin.math.max

@Composable
fun HistoryScreen(
    title: String,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            !state.error.isNullOrBlank() -> {
                Text(text = state.error.orEmpty())
            }

            state.sessions.isEmpty() -> {
                Text(text = "No history yet")
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(state.sessions) { session ->
                        HistorySessionBlock(
                            session = session,
                            onRestore = viewModel::restore
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySessionBlock(
    session: HistorySessionUi,
    onRestore: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = DateUtils.getRelativeTimeSpanString(session.timestamp).toString(),
            style = MaterialTheme.typography.labelMedium
        )

        Surface(
            tonalElevation = 1.dp,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                session.entries.forEach { entry ->
                    HistoryEntryRow(
                        entry = entry,
                        onRestore = onRestore
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryRow(
    entry: HistoryEntryUi,
    onRestore: (String) -> Unit
) {
    var expanded by rememberSaveable(entry.id) {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = entry.label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )

            if (entry.canRestore) {
                Button(
                    onClick = { onRestore(entry.id) }
                ) {
                    Text(text = "Restore")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            HistoryChangeDiff(
                oldValue = entry.oldValue,
                newValue = entry.newValue,
                expanded = expanded
            )
        }
    }
}

@Composable
private fun HistoryChangeDiff(
    oldValue: String?,
    newValue: String?,
    expanded: Boolean
) {
    when {
        oldValue == null && newValue != null -> {
            Text(
                text = "Added: $newValue",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        oldValue != null && newValue == null -> {
            Text(
                text = "Cleared",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
        }

        else -> {
            val isLong = max(oldValue?.length ?: 0, newValue?.length ?: 0) > 40

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = oldValue.orEmpty(),
                    maxLines = if (expanded) Int.MAX_VALUE else if (isLong) 2 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "→",
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = newValue.orEmpty(),
                    maxLines = if (expanded) Int.MAX_VALUE else if (isLong) 3 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}