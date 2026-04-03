package com.meadow.feature.catalog.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowBottomSheet
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.ui.components.MeadowButtonType
import com.meadow.core.ui.components.MeadowChip
import com.meadow.core.ui.components.MeadowChipType
import com.meadow.core.ui.components.MeadowDialog
import com.meadow.core.ui.components.MeadowDialogScaffold
import com.meadow.core.ui.components.MeadowDialogType
import com.meadow.core.ui.components.MeadowTextField
import com.meadow.feature.catalog.R as R
import com.meadow.feature.catalog.domain.model.CatalogType
import com.meadow.feature.catalog.internal.schema.CatalogSchema
import com.meadow.feature.catalog.ui.util.CatalogSchemaUiResolver
import com.meadow.feature.project.domain.model.ProjectType

/* ─── DELETE CONFIRMATION ───────────────────── */
@Composable
fun CatalogDeleteConfirmDialog(
    catalogTitle: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialog(
        type = MeadowDialogType.Alert,
        title = stringResource(
            R.string.catalog_delete_title,
            catalogTitle
        ),
        message = stringResource(
            R.string.catalog_delete_message
        ),
        onConfirm = onConfirmDelete,
        onDismiss = onDismiss
    )
}

/* ─── CATALOG TYPE SELECTION ───────────────────── */

@Composable
fun CatalogTypeDialog(
    availableTypes: List<CatalogType>,
    selectedType: CatalogType?,
    schemas: List<CatalogSchema>,
    resolver: CatalogSchemaUiResolver,
    onTypeSelected: (CatalogType) -> Unit,
    onSchemaSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowDialogScaffold(
        onDismiss = onDismiss,
        title = {
            Text(stringResource(R.string.choose_catalog_type))
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                items(
                    items = availableTypes,
                    key = { it.name }
                ) { type ->
                    val ui = type.toUiModel()
                    CatalogTypeOption(
                        label = ui.label,
                        icon = ui.icon
                    ) {
                        onTypeSelected(type)
                    }
                }
            }

            /* ─── SCHEMA (appears AFTER type selection) ───────── */
            if (selectedType != null && schemas.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.choose_schema),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(
                        items = schemas,
                        key = { it.schemaId }
                    ) { schema ->
                        val ui = schema.toUiModel(resolver)
                        CatalogSchemaOption(
                            label = ui.label,
                            icon = ui.icon
                        ) {
                            onSchemaSelected(schema.schemaId)
                        }
                    }
                }
            }
        },
        dismissButton = {
            MeadowButton(
                text = stringResource(CoreUiR.string.action_cancel),
                type = MeadowButtonType.Ghost,
                onClick = onDismiss
            )
        }
    )
}

@Composable
private fun CatalogTypeOption(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/* ─── INTERNAL UI MAPPING ───────────────────── */

private data class CatalogTypeUi(
    val label: String,
    val icon: ImageVector
)

@Composable
private fun CatalogType.toUiModel(): CatalogTypeUi =
    when (this) {
        CatalogType.ANIMAL ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_animal),
                icon = Icons.Outlined.Pets
            )

        CatalogType.CHARACTER ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_character),
                icon = Icons.Outlined.Face
            )

        CatalogType.LOCATION ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_location),
                icon = Icons.Outlined.Place
            )

        CatalogType.ORGANIZATION ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_organization),
                icon = Icons.Outlined.Apartment
            )

        CatalogType.PROP ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_prop),
                icon = Icons.Outlined.Inventory2
            )

        CatalogType.WARDROBE ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_wardrobe),
                icon = Icons.Outlined.Checkroom
            )

        CatalogType.VEHICLE ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_vehicle),
                icon = Icons.Outlined.DirectionsCar
            )

        CatalogType.ACTOR ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_actor),
                icon = Icons.Outlined.TheaterComedy
            )

        CatalogType.CAST_MEMBER ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_cast_member),
                icon = Icons.Outlined.Groups
            )

        CatalogType.INVENTORY_ITEM ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_inventory_item),
                icon = Icons.Outlined.Backpack
            )

        CatalogType.SKILL ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_skill),
                icon = Icons.Outlined.AutoFixHigh
            )

        CatalogType.COLOR_GUIDE ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_color_guide),
                icon = Icons.Outlined.Palette
            )

        CatalogType.STYLE_GUIDE ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_style_guide),
                icon = Icons.Outlined.Style
            )

        CatalogType.HAIR_FX ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_hair_fx),
                icon = Icons.Outlined.ContentCut
            )

        CatalogType.MAKEUP_FX ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_makeup_fx),
                icon = Icons.Outlined.Brush
            )

        CatalogType.SET ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_set),
                icon = Icons.Outlined.Theaters
            )

        CatalogType.SOUND_FX ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_sound_fx),
                icon = Icons.Outlined.GraphicEq
            )

        CatalogType.STUNT_FX ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_stunt_fx),
                icon = Icons.Outlined.FitnessCenter
            )

        CatalogType.VISUAL_FX ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_visual_fx),
                icon = Icons.Outlined.AutoAwesome
            )

        else ->
            CatalogTypeUi(
                label = stringResource(R.string.catalog_type_generic),
                icon = Icons.Outlined.HelpOutline
            )

    }

@Composable
private fun rememberCatalogTypesForProject(
    projectType: ProjectType
): List<CatalogType> =
    CatalogType.entries.filter { type ->
        when (projectType) {
            ProjectType.COMIC ->
                type in listOf(
                    CatalogType.ANIMAL,
                    CatalogType.CHARACTER,
                    CatalogType.LOCATION,
                    CatalogType.ORGANIZATION,
                    CatalogType.PROP,
                    CatalogType.WARDROBE,
                    CatalogType.VEHICLE,
                    CatalogType.COLOR_GUIDE
                )

            ProjectType.GAME ->
                type in listOf(
                    CatalogType.ANIMAL,
                    CatalogType.CHARACTER,
                    CatalogType.LOCATION,
                    CatalogType.ORGANIZATION,
                    CatalogType.PROP,
                    CatalogType.WARDROBE,
                    CatalogType.VEHICLE,
                    CatalogType.INVENTORY_ITEM,
                    CatalogType.SKILL
                )

            ProjectType.NOVEL ->
                type in listOf(
                    CatalogType.ANIMAL,
                    CatalogType.CHARACTER,
                    CatalogType.LOCATION,
                    CatalogType.ORGANIZATION,
                    CatalogType.PROP,
                    CatalogType.WARDROBE,
                    CatalogType.VEHICLE,
                    CatalogType.STYLE_GUIDE
                )

            ProjectType.TV_SHOW,
            ProjectType.MOVIE ->
                type in listOf(
                    CatalogType.ANIMAL,
                    CatalogType.CHARACTER,
                    CatalogType.LOCATION,
                    CatalogType.ORGANIZATION,
                    CatalogType.PROP,
                    CatalogType.WARDROBE,
                    CatalogType.VEHICLE,
                    CatalogType.ACTOR,
                    CatalogType.CAST_MEMBER,
                    CatalogType.HAIR_FX,
                    CatalogType.MAKEUP_FX,
                    CatalogType.SET,
                    CatalogType.SOUND_FX,
                    CatalogType.STUNT_FX,
                    CatalogType.VISUAL_FX,
                    CatalogType.STYLE_GUIDE
                )

            ProjectType.UNKNOWN ->
                false
        }
    }

/* ─── MULTIPLE SCHEMAS PER CATALOG TYPE ───────────────────── */
private data class CatalogSchemaUi(
    val label: String,
    val icon: ImageVector
)

@Composable
private fun CatalogSchema.toUiModel(
    resolver: CatalogSchemaUiResolver,
    context: Context = LocalContext.current
): CatalogSchemaUi =
    CatalogSchemaUi(
        label = resolver.resolveLabel(LocalContext.current, schemaId),
        icon = Icons.Outlined.Inventory2
    )


@Composable
private fun CatalogSchemaOption(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


/* ─── SCHEMA PICKER BOTTOM SHEET ───────────────────── */

data class CatalogSchemaPickerItem(
    val schemaId: String,
    val label: String
)

@Composable
fun CatalogSchemaPickerBottomSheet(
    title: String,
    items: List<CatalogSchemaPickerItem>,
    onPick: (CatalogSchemaPickerItem) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowBottomSheet(
        title = title,
        onDismiss = onDismiss
    ) {
        items.forEach { schema ->
            MeadowChip(
                text = schema.label,
                type = MeadowChipType.Action,
                selected = false,
                onToggle = { onPick(schema) }
            )
        }
    }
}

/* ─── LINK PICKER BOTTOM SHEET ───────────────────── */

data class CatalogPickerRow(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconResId: Int
)

@Composable
fun CatalogLinkPickerBottomSheet(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    rows: List<CatalogPickerRow>,
    onPick: (CatalogPickerRow) -> Unit,
    onDismiss: () -> Unit
) {
    MeadowBottomSheet(
        title = title,
        onDismiss = onDismiss
    ) {
        MeadowTextField(
            value = query,
            onValueChange = onQueryChange
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPick(row) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(row.iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = row.title,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = row.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Image(
                    painter = painterResource(CoreUiR.drawable.ic_chevron_right),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}