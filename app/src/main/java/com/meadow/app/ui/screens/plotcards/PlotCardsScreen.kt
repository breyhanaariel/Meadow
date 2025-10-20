package com.meadow.app.ui.screens.plotcards


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meadow.app.viewmodel.PlotCardsViewModel

/**
 * PlotCardsScreen.kt
 *
 * Visual index card system for story structure.
 * - Drag reorder (future)
 * - Color themes per plot line
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlotCardsScreen(viewModel: PlotCardsViewModel) {
    val cards by viewModel.plotCards.collectAsState()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Plot Cards", style = MaterialTheme.typography.titleLarge)
        LazyVerticalGrid(columns = GridCells.Adaptive(150.dp)) {
            items(cards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(8.dp)
                        .background(Color(card.color))
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(card.title, style = MaterialTheme.typography.titleMedium)
                        Text(card.summary.take(80) + "...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.addCard() },
            modifier = Modifier.align(Alignment.End)
        ) { Text("+") }
    }
}