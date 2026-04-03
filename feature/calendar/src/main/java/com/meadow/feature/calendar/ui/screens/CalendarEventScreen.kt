@file:OptIn(ExperimentalMaterial3Api::class)

package com.meadow.feature.calendar.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.*
import com.meadow.core.ui.components.MeadowFormHeader
import com.meadow.core.ui.components.MeadowPickerType
import com.meadow.feature.calendar.R
import com.meadow.feature.calendar.ui.viewmodel.CalendarEventViewModel
import java.time.ZoneId

@Composable
fun CalendarEventScreen(
    navController: NavController,
    viewModel: CalendarEventViewModel,
    localId: String?,
    initialProjectId: String?
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(localId, initialProjectId) {
        viewModel.load(localId, initialProjectId)
    }
    MeadowFormScaffold(
        title = if (localId == null)
            stringResource(R.string.new_event)
        else
            stringResource(R.string.edit_event),
        showDelete = localId != null,
        onBack = { navController.popBackStack() },
        onSave = {
            viewModel.save {
                navController.popBackStack()
            }
        },
        onDelete = if (localId != null) {
            {
                viewModel.delete(localId) {
                    navController.popBackStack()
                }
            }
        } else null,
        saving = false
    ) {
        /* ─── TITLE ───────────────────── */
        MeadowInputField(
            value = state.title,
            onChange = viewModel::setTitle,
            singleLine = true
        )
        /* ─── LOCATION ───────────────────── */
        MeadowLocationField(
            value = state.location,
            onValueChange = viewModel::setLocation
        )
        /* ─── ALL DAY ───────────────────── */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.all_day))
            MeadowBooleanField(
                checked = state.allDay,
                onChange = viewModel::setAllDay
            )
        }
        /* ─── START DATETIME ───────────────────── */
        Text(stringResource(CoreUiR.string.label_start))
        MeadowPickerField(
            type = MeadowPickerType.DATETIME,
            value = millisToIso(state.startAtMillis)
        ) { iso: String ->
            viewModel.setStartMillis(isoToMillis(iso))
        }
        /* ─── END DATETIME ───────────────────── */
        Text(stringResource(CoreUiR.string.label_end))
        MeadowPickerField(
            type = MeadowPickerType.DATETIME,
            value = millisToIso(state.endAtMillis)
        ) { iso: String ->
            viewModel.setEndMillis(isoToMillis(iso))
        }
        /* ─── QUICK DURATION ───────────────────── */
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickDuration("+30m") {
                viewModel.extendEndByMinutes(30)
            }
            QuickDuration("+1h") {
                viewModel.extendEndByMinutes(60)
            }
            QuickDuration("+2h") {
                viewModel.extendEndByMinutes(120)
            }
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}


/* ─── QUICK DURATION BUTTON ───────────────────── */
@Composable
private fun QuickDuration(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50)
    ) {
        Text(text)
    }
}
private fun millisToIso(millis: Long): String {
    return java.time.Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .toString()
}
private fun isoToMillis(iso: String): Long {
    return java.time.LocalDateTime.parse(iso)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}