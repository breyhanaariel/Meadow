package com.meadow.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R

/* ─── FORM SCAFFOLD ───────────────────── */

@Composable
fun MeadowFormScaffold(
    title: String,
    showDelete: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)? = null,
    saving: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        MeadowFormHeader(
            title = title,
            showDelete = showDelete,
            onBack = onBack,
            onSave = onSave,
            onDelete = onDelete,
            saving = saving
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            content = content
        )
    }
}


/* ─── FORM HEADER ───────────────────── */

@Composable
fun MeadowFormHeader(
    title: String,
    showDelete: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)? = null,
    saving: Boolean
) {
    Surface(
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            /* ─── BACK ───────────────────── */
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }

            /* ─── TITLE ──────────────────── */
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            /* ─── ACTIONS ────────────────── */
            Row {

                IconButton(
                    onClick = onSave,
                    enabled = !saving
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_save),
                        contentDescription = stringResource(R.string.action_save),
                        tint = Color.Unspecified
                    )
                }

                if (showDelete && onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.action_delete),
                            tint = Color.Unspecified
                        )
                    }
                }

            }
        }
    }
}