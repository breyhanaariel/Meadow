@file:OptIn(ExperimentalMaterial3Api::class)

package com.meadow.feature.calendar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowCard
import com.meadow.core.ui.components.MeadowCardType
import com.meadow.core.ui.components.MeadowFab
import com.meadow.core.ui.components.MeadowFabStyle
import com.meadow.core.ui.components.MeadowYearMonthDropdownField
import com.meadow.core.ui.theme.LocalMeadowThemeVariant
import com.meadow.core.ui.theme.ThemeIconResolver
import com.meadow.feature.calendar.R as R
import com.meadow.feature.calendar.domain.model.CalendarEvent
import com.meadow.feature.calendar.ui.navigation.CalendarRoutes
import com.meadow.feature.calendar.ui.viewmodel.CalendarViewModel
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.state.KebabAction
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    navController: NavController,
    featureContextState: FeatureContextState,
    viewModel: CalendarViewModel,
    projectId: String?
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(projectId) {
        viewModel.setProjectContext(projectId)
    }
    val context = LocalContext.current

    LaunchedEffect(state.isSyncing) {
        featureContextState.setKebabActions(
            listOf(
                KebabAction(context.getString(R.string.action_today)) {
                    viewModel.goToToday()
                },
                KebabAction(context.getString(CoreUiR.string.action_sync)) {
                    viewModel.syncNow()
                }
            )
        )
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sheetDate by remember { mutableStateOf<LocalDate?>(null) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }
    val eventSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {

            val themeVariant = LocalMeadowThemeVariant.current

            MeadowFab(
                style = MeadowFabStyle.ImageOnly,
                painterResId = ThemeIconResolver.fabmenu(themeVariant),
                onClick = {
                    navController.navigate(
                        CalendarRoutes.createRoute(state.projectContextId)
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            CalendarHeaderRow(
                state = state,
                onMonthChange = { newMonth ->
                    viewModel.setSelectedDate(newMonth.atDay(1))
                },
                onPrevMonth = {
                    viewModel.setSelectedDate(state.visibleMonth.minusMonths(1).atDay(1))
                },
                onNextMonth = {
                    viewModel.setSelectedDate(state.visibleMonth.plusMonths(1).atDay(1))
                },
                onSync = viewModel::syncNow,
                onToday = viewModel::goToToday,
                onSearchClick = { showSearchDialog = true }
            )

            ViewModeTabs(
                mode = state.viewMode,
                onMode = viewModel::setViewMode
            )

            when (state.viewMode) {

                CalendarViewModel.CalendarViewMode.MONTH ->
                    MonthView(
                        state = state,
                        onSelectDay = {
                            viewModel.setSelectedDate(it)
                            sheetDate = it
                        },
                        onViewEvent = { selectedEvent = it },
                        onEditEvent = {
                            navController.navigate(
                                CalendarRoutes.editRoute(it.localId)
                            )
                        },
                        onDeleteEvent = {
                            viewModel.deleteEvent(it.localId)
                        }
                    )

                CalendarViewModel.CalendarViewMode.WEEK ->
                    WeekView(
                        state = state,
                        onSelectDay = viewModel::setSelectedDate,
                        onViewEvent = { selectedEvent = it },
                        onEditEvent = {
                            navController.navigate(
                                CalendarRoutes.editRoute(it.localId)
                            )
                        },
                        onDeleteEvent = {
                            viewModel.deleteEvent(it.localId)
                        }
                    )

                CalendarViewModel.CalendarViewMode.AGENDA ->
                    AgendaView(
                        state = state,
                        onViewEvent = { selectedEvent = it },
                        onEditEvent = {
                            navController.navigate(
                                CalendarRoutes.editRoute(it.localId)
                            )
                        },
                        onDeleteEvent = {
                            viewModel.deleteEvent(it.localId)
                        }
                    )
            }
        }
        if (showSearchDialog) {
            EventSearchSheet(
                events = state.eventsByDay.values.flatten(),
                onDismiss = { showSearchDialog = false },
                onOpenEvent = {
                    showSearchDialog = false
                    navController.navigate(
                        CalendarRoutes.editRoute(it.localId)
                    )
                }
            )
        }

        if (sheetDate != null) {
            ModalBottomSheet(
                onDismissRequest = { sheetDate = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                DayBottomSheet(
                    date = sheetDate!!,
                    events = state.eventsByDay[sheetDate!!].orEmpty(),
                    onAdd = {
                        navController.navigate(CalendarRoutes.CALENDAR_CREATE)
                    },
                    onViewEvent = { selectedEvent = it },
                    onEditEvent = {
                        navController.navigate(
                            CalendarRoutes.editRoute(it.localId)
                        )
                    },
                    onDeleteEvent = {
                        viewModel.deleteEvent(it.localId)
                    }
                )
            }
        }

        if (selectedEvent != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedEvent = null },
                sheetState = eventSheetState,
                containerColor = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                EventDetailBottomSheet(
                    event = selectedEvent!!,
                    onEdit = {
                        val id = selectedEvent!!.localId
                        selectedEvent = null
                        navController.navigate(
                            CalendarRoutes.editRoute(id)
                        )
                    }
                )
            }
        }
    }
}

/* ─── HEADER ───────────────────── */

@Composable
private fun CalendarHeaderRow(
    state: CalendarViewModel.UiState,
    onMonthChange: (YearMonth) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    onSync: () -> Unit,
    onSearchClick: () -> Unit
) {
    val month = state.visibleMonth

    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val months = Month.values()
    val years = (1990..2050)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onPrevMonth) {
            Icon(Icons.Default.KeyboardArrowLeft, null)
        }

        Spacer(Modifier.weight(1f))

        val totalEventsInMonth =
            state.eventsByDay
                .filterKeys { it.month == month.month && it.year == month.year }
                .values
                .sumOf { it.size }

        var expanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            MeadowYearMonthDropdownField(
                selected = month,
                onSelected = { onMonthChange(it) },
                modifier = Modifier.widthIn(min = 240.dp)
            )

            if (totalEventsInMonth > 0) {
                Spacer(Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        totalEventsInMonth.toString(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        IconButton(onClick = onSearchClick) {
            Icon(
                painter = painterResource(CoreUiR.drawable.ic_search),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Spacer(Modifier.weight(1f))

        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.KeyboardArrowRight, null)
        }
    }
}

/* ─── MONTH VIEW ───────────────────── */
@Composable
private fun MonthView(
    state: CalendarViewModel.UiState,
    onSelectDay: (LocalDate) -> Unit,
    onViewEvent: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit
) {
    val month = state.visibleMonth
    val today = LocalDate.now()

    val firstDay = month.atDay(1)
    val length = month.lengthOfMonth()
    val days = (1..length).map { month.atDay(it) }

    val monthEvents = state.eventsByDay
        .filterKeys { it.month == month.month && it.year == month.year }
        .toSortedMap()

    val upcoming = monthEvents.filterKeys { !it.isBefore(today) }
    val past = monthEvents.filterKeys { it.isBefore(today) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        /* ─── WEEKDAY HEADER ───────────────────── */

        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayOfWeek.values().forEach {
                    Text(
                        it.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        /* ─── GRID ───────────────────── */

        item {

            val firstWeekdayOffset = (firstDay.dayOfWeek.value % 7)
            val totalCells = firstWeekdayOffset + days.size
            val rows = (totalCells + 6) / 7

            var dayIndex = 0

            Column {

                repeat(rows) { rowIndex ->

                    Row(Modifier.fillMaxWidth()) {

                        for (col in 0 until 7) {

                            val cellIndex = rowIndex * 7 + col

                            if (cellIndex < firstWeekdayOffset || dayIndex >= days.size) {

                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                )

                            } else {

                                val day = days[dayIndex++]
                                val selected = day == state.selectedDate
                                val count = state.eventsByDay[day]?.size ?: 0

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(4.dp)
                                        .background(
                                            if (selected)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { onSelectDay(day) }
                                        .padding(8.dp)
                                ) {

                                    Column(
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxSize()
                                    ) {

                                        Text(
                                            day.dayOfMonth.toString(),
                                            style = MaterialTheme.typography.labelMedium
                                        )

                                        if (count > 0) {
                                            Icon(
                                                painter = painterResource(CoreUiR.drawable.ic_star),
                                                contentDescription = null,
                                                tint = Color.Unspecified,
                                                modifier = Modifier.align(Alignment.CenterHorizontally).size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /* ─── UPCOMING SECTION ───────────────────── */

        if (upcoming.isNotEmpty()) {

            item {
                SectionHeader(stringResource(R.string.upcoming_event))
            }

            upcoming.forEach { (day, events) ->

                item {
                    DayHeader(day)
                }

                items(events) { event ->
                    EventRow(
                        event = event,
                        onView = { onViewEvent(event) },
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }
        }

        /* ─── PAST SECTION ───────────────────── */

        if (past.isNotEmpty()) {

            item {
                SectionHeader(stringResource(R.string.past_event))            }

            past.forEach { (day, events) ->

                item {
                    DayHeader(day)
                }

                items(events) { event ->
                    EventRow(
                        event = event,
                        onView = { onViewEvent(event) },
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }
        }

        if (monthEvents.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_events),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/* ─── MONTH LIST HELPERS ───────────────────── */

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun DayHeader(day: LocalDate) {
    Text(
        text = day.format(
            DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        style = MaterialTheme.typography.titleSmall
    )
}

/* ─── WEEK VIEW ───────────────────── */

@Composable
private fun WeekView(
    state: CalendarViewModel.UiState,
    onSelectDay: (LocalDate) -> Unit,
    onViewEvent: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit
) {
    val start =
        state.selectedDate.minusDays(((state.selectedDate.dayOfWeek.value % 7)).toLong())
    val weekDays = (0 until 7).map { start.plusDays(it.toLong()) }

    Column {

        /* ─── ROW 1: DAY LABELS ───────────────────── */

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Text(
                    text = day.format(
                        DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        /* ─── ROW 2: EVENT STAR INDICATOR ───────────────────── */

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->

                val count = state.eventsByDay[day]?.size ?: 0

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (count > 0) {
                        Icon(
                            painter = painterResource(CoreUiR.drawable.ic_star),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(2.dp))

        /* ─── ROW 3: DAY NUMBERS ───────────────────── */

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (day == state.selectedDate)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .clickable { onSelectDay(day) },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(2.dp))
    }
    val hours = (0..23).toList()
    val timeFormatter = DateTimeFormatter.ofPattern("h a")

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        items(hours) { hour ->

            val label = LocalTime.of(hour, 0).format(timeFormatter)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Text(
                    text = label,
                    modifier = Modifier.width(64.dp),
                    style = MaterialTheme.typography.labelSmall
                )

                Column(Modifier.weight(1f)) {
                    state.eventsByDay[state.selectedDate]
                        ?.filter {
                            Instant.ofEpochMilli(it.startAtMillis)
                                .atZone(ZoneId.of(it.timeZone))
                                .hour == hour
                        }
                        ?.forEach { event ->
                            EventRow(
                                event = event,
                                onView = { onViewEvent(event) },
                                onEdit = { onEditEvent(event) },
                                onDelete = { onDeleteEvent(event) }
                            )
                        }
                }
            }
        }
    }
}


/* ─── BOTTOM SHEET ───────────────────── */

@Composable
private fun DayBottomSheet(
    date: LocalDate,
    events: List<CalendarEvent>,
    onAdd: () -> Unit,
    onViewEvent: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                date.format(
                    DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            FilledTonalButton(
                onClick = onAdd,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_event),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (events.isEmpty()) {
            Text(stringResource(R.string.no_events))
        } else {
            events.forEach { event ->
                EventRow(
                    event = event,
                    onView = { onViewEvent(event) },
                    onEdit = { onEditEvent(event) },
                    onDelete = { onDeleteEvent(event) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

/* ─── EVENT ROW WITH DELETE ───────────────────── */

@Composable
private fun EventRow(
    event: CalendarEvent,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val zone = ZoneId.of(event.timeZone)
    val fmt = DateTimeFormatter.ofPattern("h:mm a")
    val start = Instant.ofEpochMilli(event.startAtMillis).atZone(zone)
    val end = Instant.ofEpochMilli(event.endAtMillis).atZone(zone)

    MeadowCard(
        modifier = Modifier.fillMaxWidth(),
        type = MeadowCardType.Content
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onView() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    if (event.allDay) stringResource(R.string.all_day)
                    else "${fmt.format(start)} → ${fmt.format(end)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                event.lastSyncError?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row {

                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.ic_edit),
                        contentDescription = stringResource(CoreUiR.string.action_edit),
                        tint = Color.Unspecified
                    )
                }

                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            painter = painterResource(CoreUiR.drawable.ic_delete),
                            contentDescription = stringResource(CoreUiR.string.action_delete),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewModeTabs(
    mode: CalendarViewModel.CalendarViewMode,
    onMode: (CalendarViewModel.CalendarViewMode) -> Unit
) {
    val all = listOf(
        CalendarViewModel.CalendarViewMode.MONTH,
        CalendarViewModel.CalendarViewMode.WEEK,
        CalendarViewModel.CalendarViewMode.AGENDA
    )

    TabRow(selectedTabIndex = all.indexOf(mode)) {
        all.forEach { m ->
            Tab(
                selected = mode == m,
                onClick = { onMode(m) },
                text = {
                    Text(
                        when (m) {
                            CalendarViewModel.CalendarViewMode.MONTH ->
                                stringResource(R.string.month)
                            CalendarViewModel.CalendarViewMode.WEEK ->
                                stringResource(R.string.week)
                            CalendarViewModel.CalendarViewMode.AGENDA ->
                                stringResource(R.string.agenda)
                        }
                    )
                }
            )
        }
    }
}
@Composable
private fun AgendaView(
    state: CalendarViewModel.UiState,
    onViewEvent: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (CalendarEvent) -> Unit
) {
    val days = state.eventsByDay.keys.sorted()

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(days) { day ->

            val events = state.eventsByDay[day].orEmpty()

            MeadowCard(
                modifier = Modifier.fillMaxWidth(),
                type = MeadowCardType.Content
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        day.format(
                            DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )

                    if (events.isEmpty()) {
                        Text(stringResource(R.string.no_events))
                    } else {
                        events.forEach { event ->
                            EventRow(
                                event = event,
                                onView = { onViewEvent(event) },
                                onEdit = { onEditEvent(event) },
                                onDelete = { onDeleteEvent(event) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun EventSearchSheet(
    events: List<CalendarEvent>,
    onDismiss: () -> Unit,
    onOpenEvent: (CalendarEvent) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val zoneFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val filtered = events.filter { event ->
        val zone = ZoneId.of(event.timeZone)
        val start = Instant.ofEpochMilli(event.startAtMillis).atZone(zone)

        val dateString = start.format(zoneFormatter)

        event.title.contains(query, ignoreCase = true) ||
                (event.description?.contains(query, ignoreCase = true) == true) ||
                dateString.contains(query, ignoreCase = true)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 8.dp
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }

            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(stringResource(CoreUiR.string.search))
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filtered.isEmpty() && query.isNotBlank()) {
                    item {
                        Text(
                            text = stringResource(CoreUiR.string.no_results),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                items(filtered) { event ->
                    SearchResultCard(
                        event = event,
                        query = query,
                        onClick = { onOpenEvent(event) }
                    )
                }
            }
        }
    }
}
@Composable
private fun HighlightedText(
    text: String,
    query: String,
    style: androidx.compose.ui.text.TextStyle
) {
    if (query.isBlank()) {
        Text(text = text, style = style)
        return
    }

    val annotated = buildAnnotatedString {
        val startIndex = text.indexOf(query, ignoreCase = true)

        if (startIndex == -1) {
            append(text)
        } else {
            append(text.substring(0, startIndex))

            pushStyle(
                SpanStyle(
                    background = MaterialTheme.colorScheme.primaryContainer
                )
            )
            append(text.substring(startIndex, startIndex + query.length))
            pop()

            append(text.substring(startIndex + query.length))
        }
    }

    Text(text = annotated, style = style)
}
@Composable
private fun SearchResultCard(
    event: CalendarEvent,
    query: String,
    onClick: () -> Unit
) {
    val zone = ZoneId.of(event.timeZone)
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val start = Instant.ofEpochMilli(event.startAtMillis).atZone(zone)
    val end = Instant.ofEpochMilli(event.endAtMillis).atZone(zone)

    MeadowCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        type = MeadowCardType.Content
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            HighlightedText(
                text = event.title,
                query = query,
                style = MaterialTheme.typography.titleMedium
            )

            val dateLine = if (event.allDay)
                start.format(dateFormatter)
            else
                "${start.format(dateFormatter)} • ${start.format(timeFormatter)}"

            HighlightedText(
                text = dateLine,
                query = query,
                style = MaterialTheme.typography.bodySmall
            )

            event.description?.let {
                HighlightedText(
                    text = it,
                    query = query,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
@Composable
private fun EventDetailBottomSheet(
    event: CalendarEvent,
    onEdit: () -> Unit
) {
    val zone = ZoneId.of(event.timeZone)
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val start = Instant.ofEpochMilli(event.startAtMillis).atZone(zone)
    val end = Instant.ofEpochMilli(event.endAtMillis).atZone(zone)

    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = start.format(dateFormatter),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = if (event.allDay)
                stringResource(R.string.all_day)
            else
                "${start.format(timeFormatter)} → ${end.format(timeFormatter)}",
            style = MaterialTheme.typography.bodyMedium
        )

        event.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        FilledTonalButton(
            onClick = onEdit,
            shape = RoundedCornerShape(50)
        ) {
            Text(stringResource(CoreUiR.string.action_edit))
        }

        Spacer(Modifier.height(24.dp))
    }
}