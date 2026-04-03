package com.meadow.feature.catalog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowCard
import com.meadow.core.ui.components.MeadowCardFooter
import com.meadow.core.ui.components.MeadowCardGrid
import com.meadow.core.ui.components.MeadowCardType
import com.meadow.core.ui.components.MeadowChip
import com.meadow.core.ui.components.MeadowChipType
import com.meadow.core.ui.components.MeadowSearchField
import com.meadow.feature.catalog.R as CatalogR
import com.meadow.feature.catalog.R as R
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogType
import com.meadow.feature.catalog.ui.components.CatalogLinkPickerBottomSheet
import com.meadow.feature.catalog.ui.components.CatalogPickerRow
import com.meadow.feature.catalog.ui.components.CatalogSchemaPickerBottomSheet
import com.meadow.feature.catalog.ui.components.CatalogSchemaPickerItem
import com.meadow.feature.catalog.ui.state.CatalogItemUiModel
import com.meadow.feature.catalog.ui.state.CatalogListSort
import com.meadow.feature.catalog.ui.state.CatalogListViewMode
import com.meadow.feature.catalog.ui.util.labelRes

@Composable
fun CatalogCard(
    item: CatalogItemUiModel,
    title: String,
    schemaLabel: String,
    iconResId: Int,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDriveBackup: () -> Unit,
    onDelete: () -> Unit
) {
    MeadowCard(
        modifier = Modifier.fillMaxWidth(),
        type = MeadowCardType.Content
    ) {
        Column {

            /* ─── MAIN ROW (CLICKABLE) ─── */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpen)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Image(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2
                    )
                    Text(
                        text = schemaLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /* ─── ACTIONS ─── */
            MeadowCardFooter {
                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_edit),
                        contentDescription = stringResource(
                            CoreUiR.string.action_edit
                        ),
                        tint = Color.Unspecified
                    )
                }

                IconButton(onClick = onDriveBackup) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_download),
                        contentDescription = stringResource(
                            CoreUiR.string.action_backup
                        ),
                        tint = Color.Unspecified
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_delete),
                        contentDescription = stringResource(
                            CoreUiR.string.action_delete
                        ),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogCardGrid(
    item: CatalogItemUiModel,
    title: String,
    iconResId: Int,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDriveBackup: () -> Unit,
    onDelete: () -> Unit
) {
    MeadowCardGrid(
        modifier = Modifier.padding(4.dp),
        aspectRatio = 1f,
        type = MeadowCardType.Content
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onOpen)
        ) {

            /* ─── ICON / PLACEHOLDER ─── */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }

            /* ─── ACTION BUTTONS ─── */
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_edit),
                        contentDescription = stringResource(
                            CoreUiR.string.action_edit
                        ),
                        tint = Color.Unspecified
                    )
                }
                IconButton(
                    onClick = onDriveBackup,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_download),
                        contentDescription = stringResource(
                            CoreUiR.string.action_backup
                        ),
                        tint = Color.Unspecified
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_delete),
                        contentDescription = stringResource(
                            CoreUiR.string.action_delete
                        ),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}


@Composable
fun CatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: (() -> Unit)? = null
) {
    MeadowSearchField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = stringResource(R.string.search_catalog),
        onSearchClick = onSearchClick
    )
}

@Composable
fun CatalogEmptyState(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(text = message)
    }
}

@Composable
fun CatalogListToolbar(
    viewMode: CatalogListViewMode,
    filterActive: Boolean,
    onViewModeChange: (CatalogListViewMode) -> Unit,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ViewModeToggle(
            current = viewMode,
            onChange = onViewModeChange
        )
        AssistChip(
            onClick = onSortClick,
            label = {
              Text(stringResource(CoreUiR.string.action_sort))
            }
        )
        AssistChip(
            onClick = onFilterClick,
            label = {
                Text(stringResource(CoreUiR.string.action_filter))
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (filterActive)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
private fun ViewModeButton(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface,
            contentColor = if (selected)
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

@Composable
fun ViewModeToggle(
    current: CatalogListViewMode,
    onChange: (CatalogListViewMode) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ViewModeButton(
            icon = Icons.Filled.ViewModule,
            selected = current == CatalogListViewMode.GRID,
            onClick = { onChange(CatalogListViewMode.GRID) }
        )

        ViewModeButton(
            icon = Icons.Filled.ViewAgenda,
            selected = current == CatalogListViewMode.LIST,
            onClick = { onChange(CatalogListViewMode.LIST) }
        )
    }
}

@Composable
fun CatalogSortContent(
    currentSort: CatalogListSort,
    onSortChange: (CatalogListSort) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CatalogListSort.values().forEach { sort ->
            MeadowChip(
                text = stringResource(sort.labelRes),
                type = MeadowChipType.Filter,
                selected = currentSort == sort,
                onToggle = { onSortChange(sort) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogFilterContent(
    types: List<CatalogType>,
    selected: Set<CatalogType>,
    onChange: (Set<CatalogType>) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = stringResource(R.string.catalog_item_type),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { type ->
                val isSelected = type in selected
                MeadowChip(
                    text = stringResource(type.labelRes()),
                    type = MeadowChipType.Filter,
                    selected = isSelected,
                    onToggle = { toggled ->
                        onChange(
                            if (toggled)
                                selected + type
                            else
                                selected - type
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onClear) {
                Text(stringResource(CoreUiR.string.action_clear_filters))
            }
        }
    }
}
