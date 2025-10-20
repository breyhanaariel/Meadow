package com.meadow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.meadow.app.data.model.ScriptVersion
import com.meadow.app.ui.theme.PastelLavender
import com.meadow.app.ui.theme.PastelPink

/**
 * VersionHistoryDialog.kt
 *
 * A soft pastel modal dialog showing script version history.
 * Users can preview, restore, or delete autosaved versions.
 */

@Composable
fun VersionHistoryDialog(
    versions: List<ScriptVersion>,
    onRestore: (ScriptVersion) -> Unit,
    onDelete: (ScriptVersion) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xFFFDF5FF),
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Script Version History",
                    color = PastelLavender,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Divider(color = Color(0xFFE0CFF8), thickness = 1.dp)

                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(versions) { version ->
                        VersionItem(
                            version = version,
                            onRestore = { onRestore(version) },
                            onDelete = { onDelete(version) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = PastelLavender)
            }
        }
    )
}

/**
 * Individual row item showing script version metadata.
 */
@Composable
private fun VersionItem(
    version: ScriptVersion,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        backgroundColor = Color(0xFFFFF8FC),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onRestore)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = version.title ?: "Untitled Script Version",
                    color = PastelLavender,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Saved: ${version.timestamp}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.caption
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Version",
                    tint = PastelPink
                )
            }
        }
    }
}
