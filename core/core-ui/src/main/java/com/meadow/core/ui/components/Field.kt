@file:OptIn(ExperimentalMaterial3Api::class)

package com.meadow.core.ui.components

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import coil.compose.AsyncImage
import com.meadow.core.data.fields.*
import com.meadow.core.media.utils.MeadowMediaConfig
import com.meadow.core.media.utils.MeadowMediaPreview
import com.meadow.core.media.utils.MediaUtils
import com.meadow.core.media.utils.mediaConfig
import com.meadow.core.media.utils.saveMedia
import com.meadow.core.ui.R
import com.meadow.core.ui.browser.CustomTabsLauncher
import com.meadow.core.ui.state.*
import com.meadow.core.ui.theme.*
import com.meadow.core.ui.utils.resolveStringId
import com.meadow.core.utils.datetime.*
import java.text.DateFormatSymbols
import java.time.*
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/* Utilities */
@Composable
fun FieldWithValue.resolveLabel(): String = resolveStringId(definition.labelKey).takeIf { it != 0 }?.let { stringResource(it) } ?: definition.labelKey
object EmptyReferenceDataProvider : ReferenceDataProvider {
    override fun observeItems(schemaKey: String) = flowOf(emptyList<ReferenceItem>())
}

@Composable
fun MeadowFieldLabel(
    label: String,
    tooltipKey: String?,
    helperSpec: FieldHelperSpec?,
    onHelperClick: (FieldHelperSpec) -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.clickable(enabled = tooltipKey != null) {
                showTooltip = true
            }
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (showTooltip && tooltipKey != null) {
            Popup(onDismissRequest = { showTooltip = false }) {
                MeadowTooltip(
                    text = run {
                        val id = resolveStringId(tooltipKey)
                        if (id != 0) stringResource(id) else tooltipKey
                    }
                )
            }
        }
        helperSpec?.let { spec ->
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onHelperClick(spec) }
            )
        }
    }
}
/* Helper host */
@Composable
fun MeadowFieldHelper(
    helperSpec: FieldHelperSpec?,
    onDismiss: () -> Unit
) {
    if (helperSpec == null) return

    val context = LocalContext.current
    val text = run {
        val id = resolveStringId(helperSpec.key)
        if (id != 0) stringResource(id) else helperSpec.key
    }
    val helperUrl = FieldHelperLinks.getUrl(helperSpec.key)
    when (helperSpec.type) {
        FieldHelperType.BOTTOM_SHEET -> {
            MeadowBottomSheet(
                title = text,
                onDismiss = onDismiss
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (helperUrl != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                CustomTabsLauncher.open(context, helperUrl)
                            }
                        ) {
                            Text(text = stringResource(R.string.powerlisting_wiki))
                        }
                    }
                }
            }
        }
        FieldHelperType.MODAL -> {
            MeadowModal(
                visible = true,
                onDismissRequest = onDismiss
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (helperUrl != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                CustomTabsLauncher.open(context, helperUrl)
                            }
                        ) {
                            Text(text = stringResource(R.string.powerlisting_wiki))
                        }
                    }
                }
            }
        }
    }
}

/* Main field wrapper */
@Composable
fun MeadowField(
    field: FieldWithValue,
    referenceDataProvider: ReferenceDataProvider = EmptyReferenceDataProvider,
    onValueChange: (FieldValue) -> Unit,
    hideLabel: Boolean = false
) {
    if (!field.isVisible()) return
    var activeHelper by remember { mutableStateOf<FieldHelperSpec?>(null) }
    val tokens = MeadowFieldTokens.current
    val density = field.definition.density()
    val padding = tokens.paddingFor(density)
    val minHeight = tokens.minHeightFor(density)
    val label = field.resolveLabel()
    val tooltipKey = field.definition.tooltipKey()
    val helperSpec = field.definition.helperSpec()
    val floating = when (field.definition.kind) {
        FieldKind.SINGLE_SELECT, FieldKind.MULTI_SELECT, FieldKind.TAGS, FieldKind.BOOLEAN -> true
        else -> false
    }
    val containerModifier =
        Modifier
            .then(if (field.definition.kind != FieldKind.BOOLEAN) Modifier.fillMaxWidth() else Modifier)
            .padding(horizontal = padding, vertical = 2.dp)

    Column(modifier = containerModifier) {
        if (floating) {
            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = minHeight),
                    shape = RoundedCornerShape(22.dp),
                    color = tokens.fieldSurface,
                    border = BorderStroke(1.dp, tokens.borderIdle)
                ) {
                    val contentVerticalPadding = if (field.definition.kind == FieldKind.SINGLE_SELECT || field.definition.kind == FieldKind.MULTI_SELECT ||  field.definition.kind == FieldKind.TAGS) 2.dp else 10.dp

                    Box(
                        modifier =
                            if (field.definition.kind == FieldKind.BOOLEAN) {
                                Modifier.padding(horizontal = 12.dp, vertical = contentVerticalPadding)
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = contentVerticalPadding)
                            }
                    ) {
                        MeadowFieldContent(
                            field = field,
                            referenceDataProvider = referenceDataProvider,
                            onValueChange = onValueChange
                        )
                    }
                }

                if (!hideLabel) {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .offset(y = (-10).dp)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 6.dp)
                    ) {
                        MeadowFieldLabel(
                            label = label,
                            tooltipKey = tooltipKey,
                            helperSpec = helperSpec,
                            onHelperClick = { activeHelper = it }
                        )
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!hideLabel) {
                    MeadowFieldLabel(
                        label = label,
                        tooltipKey = tooltipKey,
                        helperSpec = helperSpec,
                        onHelperClick = { activeHelper = it }
                    )
                }
                MeadowFieldContent(
                    field = field,
                    referenceDataProvider = referenceDataProvider,
                    onValueChange = onValueChange
                )
            }
        }
    }
    MeadowFieldHelper(
        helperSpec = activeHelper,
        onDismiss = { activeHelper = null }
    )
}

/* Field content switch */
@Composable
fun MeadowFieldContent(
    field: FieldWithValue,
    referenceDataProvider: ReferenceDataProvider,
    onValueChange: (FieldValue) -> Unit
) {
    val raw = field.value?.rawValue.orEmpty()
    fun setRaw(value: String?) { onValueChange(field.updated(value)) }
    when (field.definition.kind) {
        FieldKind.TEXT, FieldKind.MULTILINE_TEXT, FieldKind.EMAIL, FieldKind.URL, FieldKind.NUMBER, FieldKind.PHONE, FieldKind.CURRENCY -> {
            val keyboard =
                when (field.definition.kind) {
                    FieldKind.EMAIL -> KeyboardType.Email
                    FieldKind.URL -> KeyboardType.Uri
                    FieldKind.NUMBER -> KeyboardType.Number
                    FieldKind.PHONE -> KeyboardType.Phone
                    FieldKind.CURRENCY -> KeyboardType.Decimal
                    else -> KeyboardType.Text
                }
            val multiline = field.definition.kind == FieldKind.MULTILINE_TEXT
            MeadowInputField(
                value = raw,
                keyboardType = keyboard,
                singleLine = !multiline,
                minLines = if (multiline) 4 else 1
            ) { setRaw(it) }
        }
        FieldKind.BOOLEAN ->
            MeadowBooleanField(
                checked = raw.toBooleanStrictOrNull() ?: false,
                onChange = { setRaw(it.toString()) }
            )
        FieldKind.DATE ->
            MeadowPickerField(MeadowPickerType.DATE, raw) { setRaw(it) }
        FieldKind.TIME ->
            MeadowPickerField(MeadowPickerType.TIME, raw) { setRaw(it) }
        FieldKind.DATETIME ->
            MeadowPickerField(MeadowPickerType.DATETIME, raw) { setRaw(it) }
        FieldKind.LOCATION ->
            MeadowLocationField(raw) { setRaw(it) }
        FieldKind.SINGLE_SELECT, FieldKind.MULTI_SELECT, FieldKind.TAGS ->
            MeadowSelectField(field, raw) { setRaw(it) }
        FieldKind.RELATION ->
            MeadowRelationField(field, onValueChange)
        FieldKind.REFERENCE ->
            MeadowReferenceField(field, referenceDataProvider, onValueChange)
        FieldKind.COLOR ->
            MeadowColorField(raw) { setRaw(it) }
        FieldKind.RATING ->
            MeadowRatingField(
                rating = raw.toIntOrNull() ?: 0,
                onChange = { setRaw(it.toString()) }
            )
        FieldKind.IMAGE, FieldKind.VIDEO, FieldKind.AUDIO, FieldKind.PDF, FieldKind.FILE, FieldKind.SUBTITLE ->
            MeadowUploadField(field, onValueChange)
        FieldKind.GROUP ->
            MeadowGroupField(field, referenceDataProvider, onValueChange)
    }
}
@Composable
fun MeadowSelectField(
    field: FieldWithValue,
    raw: String,
    onChange: (String?) -> Unit
) {
    val selected =
        if (field.definition.kind == FieldKind.SINGLE_SELECT)
            raw.takeIf { it.isNotBlank() }?.let(::listOf) ?: emptyList()
        else
            raw.split(",").map(String::trim).filter(String::isNotBlank)
    MeadowDropdown(
        options = field.definition.options(),
        selected = selected,
        multiSelect = field.definition.kind != FieldKind.SINGLE_SELECT,
        onChange = { list ->
            val newRaw =
                if (field.definition.kind == FieldKind.SINGLE_SELECT)
                    list.firstOrNull()
                else
                    list.joinToString(",")
            onChange(newRaw)
        }
    )
}
@Composable
fun MeadowGroupField(
    field: FieldWithValue,
    referenceDataProvider: ReferenceDataProvider,
    onValueChange: (FieldValue) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        field.definition.children().forEach { childDef ->
            val childValue = field.childValue(childDef.id)
            MeadowField(
                field = FieldWithValue(
                    definition = childDef,
                    value = childValue,
                    allValues = field.allValues
                ),
                referenceDataProvider = referenceDataProvider,
                onValueChange = onValueChange
            )
        }
    }
}

/* Unified input field replacing text + number fields */
@Composable
fun MeadowInputField(
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    onChange: (String) -> Unit
) {
    val tokens = MeadowFieldTokens.current
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(22.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = tokens.textPrimary,
            unfocusedTextColor = tokens.textPrimary,
            focusedBorderColor = tokens.borderFocused,
            unfocusedBorderColor = tokens.borderIdle,
            focusedContainerColor = tokens.fieldSurface,
            unfocusedContainerColor = tokens.fieldSurface
        )
    )
}

/* Boolean field */
@Composable
fun MeadowBooleanField(
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onChange,
        colors = SwitchDefaults.colors()
    )
}

/* Date field with calendar picker */
/* Shared picker dialog system */
enum class MeadowPickerType { DATE, TIME, DATETIME, MONTH_YEAR }

@Composable
fun MeadowPickerField(
    type: MeadowPickerType,
    value: String,
    onValueChange: (String) -> Unit
) {
    var open by remember { mutableStateOf(false) }

    val displayText =
        when (type) {
            MeadowPickerType.DATE -> DateUtils.formatForDisplay(value)
            MeadowPickerType.TIME -> value
            MeadowPickerType.DATETIME -> {
                val date = value.substringBefore("T", "")
                val time = value.substringAfter("T", "")
                if (date.isBlank() && time.isBlank()) ""
                else "${DateUtils.formatForDisplay(date)} • $time"
            }
            MeadowPickerType.MONTH_YEAR -> value
        }
    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { open = true }) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null
                )
            }
        }
    )
    if (!open) return
    when (type) {
        MeadowPickerType.DATE -> {
            val state = rememberDatePickerState(
                initialSelectedDateMillis = DateUtils.isoToMillis(value)
            )
            DatePickerDialog(
                onDismissRequest = { open = false },
                confirmButton = {
                    TextButton(onClick = {
                        state.selectedDateMillis?.let {
                            onValueChange(DateUtils.millisToIso(it))
                        }
                        open = false
                    }) {
                        Text(stringResource(R.string.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { open = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            ) {
                DatePicker(state = state)
            }
        }
        MeadowPickerType.TIME -> {
            var hour by remember { mutableStateOf(12) }
            var minute by remember { mutableStateOf(0) }
            var am by remember { mutableStateOf(true) }
            AlertDialog(
                onDismissRequest = { open = false },
                title = { Text(stringResource(R.string.label_time)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MeadowInputField(
                            value = hour.toString(),
                            keyboardType = KeyboardType.Number
                        ) {
                            it.toIntOrNull()?.let { h ->
                                if (h in 1..12) hour = h
                            }
                        }
                        MeadowInputField(
                            value = minute.toString(),
                            keyboardType = KeyboardType.Number
                        ) {
                            it.toIntOrNull()?.let { m ->
                                if (m in 0..59) minute = m
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = am,
                                onClick = { am = true },
                                label = { Text("AM") }
                            )
                            FilterChip(
                                selected = !am,
                                onClick = { am = false },
                                label = { Text("PM") }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            TimeUtils.toIso(hour, minute, am)?.let {
                                onValueChange(it)
                            }
                            open = false
                        }
                    ) {
                        Text(stringResource(R.string.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { open = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }
        MeadowPickerType.DATETIME -> {
            var date by remember { mutableStateOf(value.substringBefore("T", "")) }
            var time by remember { mutableStateOf(value.substringAfter("T", "")) }
            AlertDialog(
                onDismissRequest = { open = false },
                title = { Text(stringResource(R.string.label_date)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        MeadowPickerField(
                            MeadowPickerType.DATE,
                            date
                        ) { date = it }

                        MeadowPickerField(
                            MeadowPickerType.TIME,
                            time
                        ) { time = it }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (date.isNotBlank() && time.isNotBlank()) {
                                onValueChange("$date" + "T" + time)
                            }
                            open = false
                        }
                    ) {
                        Text(stringResource(R.string.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { open = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }
        MeadowPickerType.MONTH_YEAR -> {
            var month by remember { mutableStateOf(Month.JANUARY) }
            var year by remember { mutableStateOf(Year.now().value) }
            AlertDialog(
                onDismissRequest = { open = false },
                title = { Text(stringResource(R.string.label_year)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onValueChange("$year-${month.value}")
                            open = false
                        }
                    ) {
                        Text(stringResource(R.string.action_ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { open = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            )
        }
    }
}

/* Dropdown with comma-separated summary */
@Composable
fun MeadowDropdown(
    options: List<String>,
    selected: List<String>,
    multiSelect: Boolean,
    onChange: (List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val resolvedSelected = selected.map { key ->
        val id = resolveStringId(key)
        if (id != 0) stringResource(id) else key
    }

    val displayText = resolvedSelected.joinToString(", ")

    Box {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            },
            singleLine = true,
            maxLines = 1
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val isSelected = selected.contains(option)
                val optionText = run {
                    val id = resolveStringId(option)
                    if (id != 0) stringResource(id) else option
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionText,
                            color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                        )
                    },
                    onClick = {
                        val updated =
                            if (multiSelect) {
                                if (isSelected) selected - option else selected + option
                            } else {
                                listOf(option)
                            }
                        onChange(updated)
                        if (!multiSelect) expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MeadowSelectRow(
    text: String,
    placeholder: Boolean,
    onClick: () -> Unit
) {
    val tokens = MeadowFieldTokens.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color =
                if (placeholder)
                    tokens.textPlaceholder
                else
                    tokens.textPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = tokens.textPlaceholder
        )
    }
}

@Composable
fun MeadowEntitySelectorDialog(
    title: String,
    items: List<ReferenceItem>,
    multi: Boolean,
    initialSelection: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selection by remember(initialSelection) {
        mutableStateOf(initialSelection.toMutableSet())
    }
    val filtered =
        remember(items, search) {
            val q = search.trim().lowercase()
            if (q.isBlank()) items
            else items.filter { it.label.lowercase().contains(q) }
        }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.search))
                    },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                LazyColumn {
                    items(filtered, key = { it.id }) { item ->
                        val checked = selection.contains(item.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (multi) {
                                        if (checked) selection.remove(item.id)
                                        else selection.add(item.id)
                                    } else {
                                        selection.clear()
                                        selection.add(item.id)
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selection) }
            ) {
                Text(stringResource(R.string.action_done))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

/* Relation field */
@Composable
fun MeadowRelationField(
    field: FieldWithValue,
    onValueChange: (FieldValue) -> Unit
) {
    val raw = field.value?.rawValue.orEmpty()
    val selectedIds =
        raw.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    var open by remember { mutableStateOf(false) }
    val items =
        remember {
            selectedIds.map { ReferenceItem(it, it) }
        }
    MeadowSelectRow(
        text =
            if (selectedIds.isEmpty())
                stringResource(R.string.action_select)
            else
                selectedIds.joinToString(", "),
        placeholder = selectedIds.isEmpty(),
        onClick = { open = true }
    )
    if (!open) return
    MeadowEntitySelectorDialog(
        title = stringResource(R.string.action_select),
        items = items,
        multi = true,
        initialSelection = selectedIds.toSet(),
        onConfirm = {
            val rawValue =
                it.joinToString(",").ifBlank { null }
            onValueChange(field.updated(rawValue))
            open = false
        },
        onDismiss = { open = false }
    )
}

/* Reference field using your prior logic */
@Composable
fun MeadowReferenceField(
    field: FieldWithValue,
    referenceDataProvider: ReferenceDataProvider,
    onValueChange: (FieldValue) -> Unit
) {
    val metadata = field.definition.metadata
    val targetSchemas: List<String> =
        when (val raw = metadata["targetSchema"]) {
            is String -> listOf(raw)
            is List<*> -> raw.filterIsInstance<String>()
            else -> emptyList()
        }
    val multi = (metadata["multi"] as? Boolean) ?: false
    if (targetSchemas.isEmpty()) {
        Text(
            text = stringResource(
                R.string.label_missing_target_schema,
                field.definition.id
            ),
            color = MaterialTheme.colorScheme.error
        )
        return
    }
    val raw = field.value?.rawValue.orEmpty()
    val selectedIds =
        raw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    val summary =
        when {
            selectedIds.isEmpty() -> stringResource(R.string.none)
            !multi -> selectedIds.first()
            else -> "${selectedIds.size} ${stringResource(R.string.selected)}"
        }
    var open by remember { mutableStateOf(false) }
    MeadowSelectRow(
        text = summary,
        placeholder = selectedIds.isEmpty(),
        onClick = { open = true }
    )
    if (!open) return
    val items by remember(targetSchemas) {
        targetSchemas
            .map { schema -> referenceDataProvider.observeItems(schema) }
            .reduce { acc, flow -> combine(acc, flow) { a, b -> a + b } }

    }.collectAsState(initial = emptyList())
    MeadowEntitySelectorDialog(
        title = stringResource(
            R.string.select_target,
            targetSchemas.joinToString(", ")
        ),
        items = items,
        multi = multi,
        initialSelection = selectedIds.toSet(),
        onConfirm = {
            val rawValue =
                it.joinToString(",").ifBlank { null }
            onValueChange(field.updated(rawValue))
            open = false
        },
        onDismiss = { open = false }
    )
}

/* Color field with hex input, preview background, and inline visual picker */
@Composable
fun MeadowColorField(
    value: String,
    onChange: (String) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    var text by remember(value) { mutableStateOf(value) }
    fun formatHexInput(input: String): String {
        val cleaned = input
            .uppercase()
            .replace("#", "")
            .filter { it in "0123456789ABCDEF" }
        return if (cleaned.isBlank()) "" else "#${cleaned.take(8)}"
    }
    fun parseHexColorOrNull(hex: String) = runCatching { Color(AndroidColor.parseColor(hex)) }.getOrNull()
    fun colorToHex(color: Color) = String.format("#%02X%02X%02X%02X", (color.alpha * 255).toInt(), (color.red * 255).toInt(), (color.green * 255).toInt(), (color.blue * 255).toInt())
    val parsedColor = parseHexColorOrNull(text)
    val displayColor = parsedColor ?: MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (parsedColor != null && displayColor.luminance() < 0.5f) {
            Color.White
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                val formatted = formatHexInput(input)
                text = formatted
                onChange(formatted)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = displayColor,
                    shape = RoundedCornerShape(16.dp)
                ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = contentColor),
            label = {
                Text(
                    text = stringResource(R.string.label_hex),
                    color = contentColor
                )
            },
            trailingIcon = {
                Row {
                    if (text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                text = ""
                                onChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = stringResource(R.string.action_clear),
                                tint = contentColor
                            )
                        }
                    }
                    IconButton(
                        onClick = { showPicker = true }
                    ) {
                        Text(
                            text = stringResource(R.string.label_pick_color),
                            color = contentColor
                        )
                    }
                }
            }
        )
        Box(
            modifier = Modifier
                .height(6.dp)
                .fillMaxWidth()
                .background(displayColor, RoundedCornerShape(50))
        )
    }
    if (showPicker) {
        var red by remember { mutableFloatStateOf(displayColor.red) }
        var green by remember { mutableFloatStateOf(displayColor.green) }
        var blue by remember { mutableFloatStateOf(displayColor.blue) }
        val selected = Color(red, green, blue)
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = {
                Text(text = stringResource(R.string.label_pick_color))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(stringResource(R.string.label_red, (red * 255).toInt()))
                        Slider(value = red, onValueChange = { red = it })
                    }

                    Column {
                        Text(stringResource(R.string.label_green, (green * 255).toInt()))
                        Slider(value = green, onValueChange = { green = it })
                    }

                    Column {
                        Text(stringResource(R.string.label_blue, (blue * 255).toInt()))
                        Slider(value = blue, onValueChange = { blue = it })
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(selected, RoundedCornerShape(12.dp))
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hex = colorToHex(selected)
                        text = hex
                        onChange(hex)
                        showPicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPicker = false }
                ) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

/* Rating field with icons */
@Composable
fun MeadowRatingField(
    rating: Int,
    onChange: (Int) -> Unit
) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector =
                    if (index < rating) Icons.Outlined.Star
                    else Icons.Outlined.StarBorder,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onChange(index + 1) }
                    .padding(2.dp)
            )
        }
    }
}

/* Location field */
@Composable
fun MeadowLocationField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    var text by remember(value) { mutableStateOf(value) }
    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            text = input
            onValueChange(input)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            Row {
                if (text.isNotBlank()) {
                    IconButton(
                        onClick = {
                            text = ""
                            onValueChange("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(R.string.action_clear)
                        )
                    }
                    IconButton(
                        onClick = {
                            val uri = Uri.parse(
                                "https://www.google.com/maps/search/?api=1&query=${Uri.encode(text)}"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = stringResource(R.string.label_open_in_maps)
                        )
                    }
                    IconButton(
                        onClick = {
                            val uri = Uri.parse(
                                "https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(text)}"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = stringResource(R.string.label_get_directions)
                        )
                    }
                }
            }
        }
    )
}

/* Upload field with upload, replace, delete, and image preview */
@Composable
fun MeadowUploadField(
    field: FieldWithValue,
    onValueChange: (FieldValue) -> Unit
) {

    val context = LocalContext.current
    val path = field.value?.rawValue.orEmpty()

    val config: MeadowMediaConfig =
        remember(field.definition.kind) {
            mediaConfig(field.definition.kind.name)
        }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            uri ?: return@rememberLauncherForActivityResult

            val newPath =
                saveMedia(
                    context = context,
                    uri = uri,
                    folder = config.folder
                )

            onValueChange(
                FieldValue(
                    fieldId = field.definition.id,
                    ownerItemId = field.value?.ownerItemId ?: "",
                    rawValue = newPath
                )
            )
        }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        if (path.isBlank()) {

            OutlinedButton(
                onClick = { launcher.launch(config.mimeType) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.upload))
            }

            return
        }

        MeadowMediaPreview(
            path = path,
            kind = field.definition.kind.name
        )

        Row {

            TextButton(
                onClick = { launcher.launch(config.mimeType) }
            ) {
                Text(stringResource(R.string.action_replace))
            }

            TextButton(
                onClick = { onValueChange(field.updated(null)) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.action_delete))
            }
        }
    }
}
