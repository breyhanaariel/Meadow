package com.meadow.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meadow.app.domain.models.CalendarEvent
import com.meadow.app.ui.components.PastelButton
import com.meadow.app.ui.theme.*
import com.meadow.app.viewmodel.CalendarViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * CalendarScreen.kt
 *
 * Upgraded pastel calendar with:
 * - Recurring events (daily, weekly, monthly)
 * - Event reminders
 * - Google Calendar sync toggle
 * - Sparkle highlights on event days
 * - Glitter animation when saving/syncing
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    projectId: String,
    calendarViewModel: CalendarViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val events by calendarViewModel.getEventsForProject(projectId).collectAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }
    var glitterActive by remember { mutableStateOf(false) }
    var syncEnabled by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Calendar 📅", color = TextPrimary) },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Google Sync", color = TextSecondary, fontSize = 12.sp)
                        Switch(
                            checked = syncEnabled,
                            onCheckedChange = { syncEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PastelMint,
                                uncheckedThumbColor = PastelBlue
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PastelLavender,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        },
        containerColor = BackgroundLight
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MeadowGradients.LavenderPink)
                .padding(padding)
                .padding(16.dp)
        ) {
            Column {
                MonthViewWithSparkles(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    eventDates = events.map { LocalDate.parse(it.date) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Events on ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val dayEvents = events.filter {
                    it.date == selectedDate.toString() || it.matchesRecurring(selectedDate)
                }

                if (dayEvents.isEmpty()) {
                    Text("No events today 🌸", color = TextSecondary, fontSize = 14.sp)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(dayEvents.size) { index ->
                            val event = dayEvents[index]
                            EventCard(
                                event = event,
                                onEdit = { selectedEvent = event },
                                onDelete = {
                                    calendarViewModel.deleteEvent(event.id)
                                    glitterActive = true
                                },
                                onClick = { selectedEvent = event }
                            )
                        }
                    }
                }
            }

            // ✨ Glitter Animation
            if (glitterActive) {
                GlitterOverlay(onFinish = { glitterActive = false })
            }
        }
    }

    // 🪄 Add/Edit Event Dialog
    if (showAddDialog) {
        EventDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, description, date, recurrence, reminder ->
                calendarViewModel.addEvent(
                    projectId, title, description, date, recurrence, reminder
                )
                glitterActive = true
                showAddDialog = false
                if (syncEnabled) {
                    coroutineScope.launch {
                        calendarViewModel.syncWithGoogleCalendar()
                    }
                }
            },
            selectedDate = selectedDate
        )
    }

    // 🌸 Event Detail Dialog
    selectedEvent?.let {
        EventDetailDialog(
            event = it,
            onDismiss = { selectedEvent = null },
            onDelete = {
                calendarViewModel.deleteEvent(it.id)
                selectedEvent = null
                glitterActive = true
            },
            onEdit = { updated ->
                calendarViewModel.updateEvent(updated)
                selectedEvent = null
                glitterActive = true
            }
        )
    }
}

/**
 * 🌸 Month View with Sparkles on Event Days
 */
@Composable
fun MonthViewWithSparkles(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    eventDates: List<LocalDate>
) {
    val currentMonth = selectedDate.month
    val daysInMonth = selectedDate.lengthOfMonth()
    val firstDay = LocalDate.of(selectedDate.year, currentMonth, 1)
    val firstWeekday = firstDay.dayOfWeek.value % 7

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = currentMonth.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(it, color = TextSecondary, fontSize = 14.sp)
            }
        }

        val totalCells = daysInMonth + firstWeekday
        val weeks = (0 until totalCells step 7)

        weeks.forEach { weekStart ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (i in 0..6) {
                    val dayIndex = weekStart + i - firstWeekday + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = LocalDate.of(selectedDate.year, currentMonth, dayIndex)
                        val isSelected = date == selectedDate
                        val hasEvent = date in eventDates

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(50))
                                .background(
                                    if (isSelected) PastelPeach else PastelBlue,
                                    RoundedCornerShape(50)
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayIndex.toString(),
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 14.sp
                                )
                                if (hasEvent) SparkleDot()
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}

/**
 * 🌟 Sparkle Dot for event days
 */
@Composable
fun SparkleDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Canvas(modifier = Modifier.size(6.dp).alpha(alpha)) {
        drawCircle(color = PastelMint, radius = size.minDimension / 2)
    }
}

/**
 * ✨ Glitter Overlay Animation
 */
@Composable
fun GlitterOverlay(onFinish: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            delay(1500)
            onFinish()
        }
    }

    val sparkleColor = listOf(Color.White.copy(alpha = 0.6f), PastelPink, PastelBlue)

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (i in 1..40) {
            val x = (0..size.width.toInt()).random().toFloat()
            val y = (0..size.height.toInt()).random().toFloat()
            val radius = (1..3).random().toFloat()
            drawCircle(
                color = sparkleColor.random(),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * 💫 Event Recurrence Helper
 */
fun CalendarEvent.matchesRecurring(date: LocalDate): Boolean {
    return when (this.recurrence) {
        "Daily" -> !LocalDate.parse(this.date).isAfter(date)
        "Weekly" -> {
            val start = LocalDate.parse(this.date)
            !start.isAfter(date) && start.dayOfWeek == date.dayOfWeek
        }
        "Monthly" -> {
            val start = LocalDate.parse(this.date)
            !start.isAfter(date) && start.dayOfMonth == date.dayOfMonth
        }
        else -> false
    }
}

/**
 * 🌸 Add/Edit Event Dialog with Recurrence & Reminder
 */
@Composable
fun EventDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, LocalDate, String, String) -> Unit,
    selectedDate: LocalDate
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf("None") }
    var reminder by remember { mutableStateOf("None") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            PastelButton(text = "Save", onClick = { onSave(title, description, selectedDate, recurrence, reminder) })
        },
        dismissButton = { PastelButton(text = "Cancel", onClick = onDismiss) },
        title = { Text("Add Event ✨") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

                DropdownSelector(
                    label = "Recurrence",
                    options = listOf("None", "Daily", "Weekly", "Monthly"),
                    selected = recurrence,
                    onSelect = { recurrence = it }
                )

                DropdownSelector(
                    label = "Reminder",
                    options = listOf("None", "10 minutes before", "1 hour before", "1 day before"),
                    selected = reminder,
                    onSelect = { reminder = it }
                )
            }
        }
    )
}

/**
 * 🌸 Simple Reusable Dropdown Selector
 */
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}