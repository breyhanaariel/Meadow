package com.meadow.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.meadow.app.domain.model.MindMapNode
import com.meadow.app.viewmodel.MindMapViewModel
import kotlin.math.roundToInt

/**
 * MindMapScreen.kt
 *
 * Visual node editor for brainstorming relationships.
 * - Pan, zoom, drag nodes
 * - Links between related ideas
 */

@Composable
fun MindMapScreen(viewModel: MindMapViewModel) {
    val nodes by viewModel.nodes.collectAsState()
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8FB))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    offset += pan
                    scale *= zoom
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            nodes.forEach { node ->
                node.links.forEach { linkedId ->
                    val target = nodes.find { it.id == linkedId } ?: return@forEach
                    drawLine(
                        color = Color(0xFFD0B3FF),
                        start = node.position * scale + offset,
                        end = target.position * scale + offset,
                        strokeWidth = 3f
                    )
                }
            }
        }

        nodes.forEach { node ->
            Box(
                modifier = Modifier
                    .offset(
                        x = (node.position.x * scale + offset.x).dp,
                        y = (node.position.y * scale + offset.y).dp
                    )
                    .size(80.dp)
                    .background(Color(0xFFE7D6FF), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(node.title, style = MaterialTheme.typography.bodySmall)
            }
        }

        FloatingActionButton(
            onClick = { viewModel.addNode("New Idea") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+")
        }
    }
}