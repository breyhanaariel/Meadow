package com.meadow.core.ai.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meadow.core.ai.domain.model.AiPersona

/* ─── PERSONA SOURCE ───────────────────── */
/* Defines which personas are selectable in chat UI */
private val chatSelectablePersonas = AiPersona.values()
    .filterNot { it == AiPersona.Bud }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AiPersonaSelector(
    selected: AiPersona,
    onSelect: (AiPersona) -> Unit,
) {

    /* ─── UI STATE ───────────────────── */
    /* Controls dropdown menu visibility */
    var expanded by remember { mutableStateOf(false) }

    /* ─── QUICK SWITCH STATE ───────────────────── */
    /* Controls long-press persona switch overlay */
    var quickSwitch by remember { mutableStateOf(false) }

    Box {

        /* ─── PERSONA PILL ───────────────────── */
        /* Primary persona selector with click + long-press behavior */
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .combinedClickable(
                    onClick = { expanded = true },
                    onLongClick = { quickSwitch = true }
                )
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected.displayName,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "▼",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        /* ─── PERSONA DROPDOWN ───────────────────── */
        /* Standard dropdown for persona selection */
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            chatSelectablePersonas.forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona.displayName) },
                    onClick = {
                        expanded = false
                        onSelect(persona)
                    }
                )
            }
        }

        /* ─── QUICK SWITCH OVERLAY ───────────────────── */
        /* Long-press radial-style overlay for fast persona switching */
        if (quickSwitch) {
            QuickPersonaSwitcherOverlay(
                selected = selected,
                onSelect = {
                    quickSwitch = false
                    onSelect(it)
                },
                onDismiss = { quickSwitch = false }
            )
        }
    }
}

/* ─── QUICK PERSONA SWITCHER ───────────────────── */
@Composable
private fun QuickPersonaSwitcherOverlay(
    selected: AiPersona,
    onSelect: (AiPersona) -> Unit,
    onDismiss: () -> Unit
) {

    /* ─── BACKDROP ───────────────────── */
    /* Dimmed overlay that dismisses on tap */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {

        /* ─── PERSONA LIST ───────────────────── */
        /* Vertical list of available personas for fast switching */
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            chatSelectablePersonas.forEach { persona ->
                Text(
                    text = persona.displayName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(persona) }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
