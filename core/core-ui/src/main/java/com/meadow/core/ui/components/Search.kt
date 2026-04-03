package com.meadow.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R

/* ─── VIEW MODE ENUM ───────────────────── */

enum class MeadowViewMode {
    GRID,
    LIST
}

/* ─── SEARCH SHEET TYPES ───────────────────── */

private enum class MeadowSearchSheet {
    VISIBILITY,
    SORT,
    FILTER
}

/* ─── SEARCH FIELD ───────────────────── */

@Composable
fun MeadowSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search),
    onSearchClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { onSearchClick?.invoke() },
            modifier = Modifier.size(52.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

/* ─── SEARCH TOOLBAR ───────────────────── */

@Composable
fun MeadowSearchToolbar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = stringResource(R.string.search),
    modifier: Modifier = Modifier,
    viewMode: MeadowViewMode,
    onViewModeChange: (MeadowViewMode) -> Unit,
    showVisibility: Boolean = true,
    showSort: Boolean = true,
    showFilter: Boolean = true,
    visibilitySheet: @Composable ColumnScope.() -> Unit = {},
    sortSheet: @Composable ColumnScope.() -> Unit = {},
    filterSheet: @Composable ColumnScope.() -> Unit = {}
) {

    var activeSheet by remember { mutableStateOf<MeadowSearchSheet?>(null) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        MeadowSearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = placeholder
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            MeadowViewModeToggle(
                current = viewMode,
                onChange = onViewModeChange
            )

            if (showVisibility) {
                AssistChip(
                    onClick = { activeSheet = MeadowSearchSheet.VISIBILITY },
                    label = { Text(stringResource(R.string.action_visibility)) }
                )
            }

            if (showSort) {
                AssistChip(
                    onClick = { activeSheet = MeadowSearchSheet.SORT },
                    label = { Text(stringResource(R.string.action_sort)) }
                )
            }

            if (showFilter) {
                AssistChip(
                    onClick = { activeSheet = MeadowSearchSheet.FILTER },
                    label = { Text(stringResource(R.string.action_filter)) }
                )
            }
        }
    }

    when (activeSheet) {

        MeadowSearchSheet.VISIBILITY -> MeadowBottomSheet(
            title = stringResource(R.string.action_visibility),
            onDismiss = { activeSheet = null }
        ) {
            visibilitySheet()
        }

        MeadowSearchSheet.SORT -> MeadowBottomSheet(
            title = stringResource(R.string.action_sort),
            onDismiss = { activeSheet = null }
        ) {
            sortSheet()
        }

        MeadowSearchSheet.FILTER -> MeadowBottomSheet(
            title = stringResource(R.string.action_filter),
            onDismiss = { activeSheet = null }
        ) {
            filterSheet()
        }

        null -> {}
    }
}

/* ─── VIEW MODE TOGGLE ───────────────────── */

@Composable
fun MeadowViewModeToggle(
    current: MeadowViewMode,
    onChange: (MeadowViewMode) -> Unit
) {

    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        MeadowViewModeButton(
            icon = Icons.Filled.ViewModule,
            selected = current == MeadowViewMode.GRID,
            onClick = { onChange(MeadowViewMode.GRID) }
        )

        MeadowViewModeButton(
            icon = Icons.Filled.ViewAgenda,
            selected = current == MeadowViewMode.LIST,
            onClick = { onChange(MeadowViewMode.LIST) }
        )
    }
}

/* ─── VIEW MODE BUTTON ───────────────────── */

@Composable
private fun MeadowViewModeButton(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor =
                if (selected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface,
            contentColor =
                if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
        )
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}