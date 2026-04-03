package com.meadow.feature.catalog.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.data.fields.FieldHelperSpec
import com.meadow.core.data.fields.FieldKind
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowFieldHelper
import com.meadow.core.ui.components.MeadowField
import com.meadow.core.ui.state.ReferenceDataProvider
import com.meadow.feature.catalog.R as R
import com.meadow.feature.project.ui.state.ProjectFormState

@Composable
fun CatalogFormContent(
    fields: List<FieldWithValue>,
    seriesId: String?,
    seriesSharedFieldIds: Set<String>,
    referenceDataProvider: ReferenceDataProvider,
    onFieldChange: (FieldWithValue) -> Unit,
    onToggleSeriesField: (fieldId: String, makeSeries: Boolean) -> Unit,
    onShowHelper: (FieldHelperSpec) -> Unit,
    primaryButtonText: String,
    onPrimaryAction: () -> Unit,
    secondaryButtonText: String?,
    onSecondaryAction: (() -> Unit)?,
    enabled: Boolean,
    showPrimaryLoading: Boolean,
    onFieldFocused: ((FieldWithValue) -> Unit)? = null,
    onFieldLongPress: ((FieldWithValue) -> Unit)? = null
) {
    /* ─── FORM LAYOUT ────────────────────────────── */
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* ── FORM FIELDS ── */
        fields.forEach { field ->
            Log.d(
                "CatalogField",
                "id=${field.definition.id}, " +
                        "key=${field.definition.key}, " +
                        "labelKey=${field.definition.labelKey}"
            )
            val canShareToSeries = seriesId != null
            val shared = field.definition.id in seriesSharedFieldIds

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                /* ─── SERIES SHARE TOGGLE ─────────────── */
                if (canShareToSeries) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.catalog_share_with_series),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Checkbox(
                            checked = shared,
                            enabled = enabled,
                            onCheckedChange = { checked ->
                                if (enabled) {
                                    onToggleSeriesField(field.definition.id, checked)
                                }
                            }
                        )
                    }
                }

                /* ─── FIELD EDITOR ───────────────────── */
                MeadowField(
                    field = field,
                    referenceDataProvider = referenceDataProvider,
                    onValueChange = { newValue: FieldValue ->
                        if (enabled) {
                            onFieldChange(field.copy(value = newValue))
                        }
                    },
                    onShowHelper = onShowHelper,
                    hideLabel = when (field.definition.kind) {
                        FieldKind.BOOLEAN,
                        FieldKind.SINGLE_SELECT,
                        FieldKind.MULTI_SELECT,
                        FieldKind.TAGS -> true
                        else -> false
                    },
                    onFocused = {
                        onFieldFocused?.invoke(field)
                    },
                    onLongPress = {
                        onFieldLongPress?.invoke(field)
                    }
                )
            }
        }

        /* ─── FORM FOOTER SPACING ─────────────────── */
        Spacer(Modifier.height(24.dp))

        /* ── ACTION BUTTONS ── */
        if (secondaryButtonText != null && onSecondaryAction != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                /* ─── PRIMARY ACTION (SAVE) ─────────── */
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                ) {
                    if (showPrimaryLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(primaryButtonText)
                    }
                }
                /* ─── SECONDARY ACTION (CANCEL) ─────── */
                OutlinedButton(
                    onClick = onSecondaryAction,
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                ) {
                    Text(secondaryButtonText)
                }
            }
        } else {
            /* ─── SINGLE PRIMARY ACTION ───────────── */
            Button(
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            ) {
                if (showPrimaryLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(primaryButtonText)
                }
            }
        }
    }
}

