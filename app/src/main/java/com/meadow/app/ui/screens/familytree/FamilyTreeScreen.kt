package com.meadow.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meadow.app.viewmodel.FamilyTreeViewModel

/**
 * FamilyTreeScreen.kt
 *
 * Displays relationships between characters.
 * - Parent-child link visualization
 * - Collapsible branches
 */

@Composable
fun FamilyTreeScreen(viewModel: FamilyTreeViewModel) {
    val relations by viewModel.tree.collectAsState()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Family Tree", style = MaterialTheme.typography.titleLarge)
        Canvas(modifier = Modifier.fillMaxSize()) {
            relations.forEach { rel ->
                rel.children.forEach { child ->
                    drawLine(
                        color = Color(0xFFDAB6FC),
                        start = Offset(rel.x, rel.y),
                        end = Offset(child.x, child.y),
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}