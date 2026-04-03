package com.meadow.feature.project.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.theme.MeadowBrand
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.KebabAction
import com.meadow.feature.project.R as R
import com.meadow.feature.project.domain.model.ChangeSource
import com.meadow.feature.project.ui.state.ProjectFieldHistoryGroupUi
import com.meadow.feature.project.ui.state.ProjectHistoryEntryUi
import com.meadow.feature.project.ui.state.ProjectHistorySessionUi
import com.meadow.feature.project.ui.viewmodel.ProjectHistoryViewModel
import kotlin.math.max

@Composable
fun ProjectHistoryScreen(
    navController: NavController,
    featureContextState: FeatureContextState,
    viewModel: ProjectHistoryViewModel = hiltViewModel()
) {
    /* ─── UI STATE ─────────────────── */
    val state by viewModel.uiState.collectAsState()

    /* ─── KEBAB ───────── */
    val backString = stringResource(CoreUiR.string.back)
    LaunchedEffect(Unit) {
        featureContextState.setKebabActions(
            listOf(KebabAction(label = backString) { navController.popBackStack() })
        )
    }

    /* ─── SCREEN CONTENT ───────────────────── */
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.history_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        when {
            state.isLoading -> BoxCenter { CircularProgressIndicator() }
            !state.error.isNullOrBlank() -> BoxCenter { Text(state.error.orEmpty()) }
            state.sessions.isEmpty() -> QuietEmptyState()
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                    items(state.sessions) { session ->
                        SessionBlock(session)
                    }
                }
            }
        }
    }
}
/* ─── SESSION GROUP a group of changes made close together in time by the same source ───────────────────── */
@Composable
private fun SessionBlock(session: ProjectHistorySessionUi) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Relative timestamp (e.g. "5 minutes ago")
        Text(
            text = DateUtils.getRelativeTimeSpanString(session.timestamp).toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // Session container
        Surface(shape = MaterialTheme.shapes.large, tonalElevation = 1.dp) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                session.fields.forEach { fieldGroup ->
                    FieldGroupBlock(fieldGroup)
                }
            }
        }
    }
}

/* ─── FIELD GROUP groups all changes for a single field within a session ───────────────────── */

@Composable
private fun FieldGroupBlock(group: ProjectFieldHistoryGroupUi) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(group.label, style = MaterialTheme.typography.titleSmall)
        group.changes.forEach { entry ->
            HistoryEntryRow(entry)
        }
    }
}

/* ─── HISTORY ENTRY ROW ───────────────────── */

@Composable
private fun HistoryEntryRow(entry: ProjectHistoryEntryUi) {
    val expandedKey = "${entry.fieldId}_${entry.changedAt}"
    var expanded by rememberSaveable(expandedKey) { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = entry.fieldLabel, style = MaterialTheme.typography.labelLarge)

            if (entry.isSeriesField) {
                Icon(
                    painter = painterResource(CoreUiR.drawable.ic_star),
                    contentDescription = stringResource(R.string.series_shared_field),
                    tint = MeadowBrand.GoldSoftDark,
                    modifier = Modifier.padding(start = 4.dp).size(14.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            SourceDot(entry.source)
            Text(
                text = entry.source.displayName(),
                style = MaterialTheme.typography.labelSmall,
                color = sourceColor(entry.source)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            ChangeDiff(entry.oldValue, entry.newValue, expanded)
        }
    }
}

/* ─── CHANGE DIFFER RENDERER handles added, cleared, and modified values ───────────────────── */

@Composable
private fun ChangeDiff(oldValue: String?, newValue: String?, expanded: Boolean) {
    when {
        oldValue == null && newValue != null ->
            Text(
                text = stringResource(R.string.history_added_value, newValue),
                style = MaterialTheme.typography.bodyMedium
            )

        oldValue != null && newValue == null ->
            Text(
                text = stringResource(R.string.history_cleared_value),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )

        else -> {
            val isLong = max(oldValue?.length ?: 0, newValue?.length ?: 0) > 40
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isLong) "“$oldValue”" else oldValue.orEmpty(),
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("→", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = if (isLong) "“$newValue”" else newValue.orEmpty(),
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/* ─── SOURCE INDICATOR ───────────────────── */

@Composable
private fun SourceDot(source: ChangeSource) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .padding(end = 4.dp)
            .background(sourceColor(source), MaterialTheme.shapes.small)
    )
}
private fun sourceColor(source: ChangeSource): Color =
    when (source) {
        ChangeSource.MANUAL -> MeadowBrand.Lavender500
        ChangeSource.SYNC -> MeadowBrand.Periwinkle300
        ChangeSource.MIGRATION -> MeadowBrand.Cream400
    }

/* ─── CHANGE SOURCE HELPERS ───────────────────── */

@Composable
private fun ChangeSource.displayName(): String =
    stringResource(
        when (this) {
            ChangeSource.MANUAL -> R.string.history_source_manual
            ChangeSource.SYNC -> R.string.history_source_sync
            ChangeSource.MIGRATION -> R.string.history_source_migration
        }
    )

/* ─── EMPTY UTILITY UI ───────────────────── */

@Composable
private fun QuietEmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.history_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BoxCenter(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}
