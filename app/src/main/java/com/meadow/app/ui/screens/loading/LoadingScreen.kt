package com.meadow.app.ui.screens.loading

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meadow.app.R
import com.meadow.app.ui.components.GlitterOverlay
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onFinished: () -> Unit) {
    val alphaAnim by rememberInfiniteTransition().animateFloat(initialValue = 0.7f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse))
    val gradientAnim by rememberInfiniteTransition().animateColor(initialValue = Color(0xFFEFD7FF), targetValue = Color(0xFFFAD7FF), animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse))
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(true) {
        repeat(18) {
            progress = (it + 1) / 18f
            delay(120)
        }
        delay(300)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(gradientAnim, Color(0xFFF4E5FF)))), contentAlignment = Alignment.Center) {
        GlitterOverlay()
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.meadow_logo_static), contentDescription = "Meadow", modifier = Modifier.size(220.dp).alpha(alphaAnim))
            Spacer(Modifier.height(24.dp))
            Box(modifier = Modifier.width(220.dp).height(14.dp).background(Brush.linearGradient(listOf(Color(0xFFDAB0FF), Color(0xFFFFBEE3))), shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.CenterStart) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).background(Color(0xFFF9E2FF), shape = RoundedCornerShape(8.dp)))
                Box(modifier = Modifier.offset(x = ((200 * progress).coerceIn(0f,200f)).dp).size(16.dp).background(Color(0xFFFFD700), shape = RoundedCornerShape(6.dp)))
            }
            Spacer(Modifier.height(12.dp))
            Text("Loading…", color = Color(0xFF8B6EC9), fontSize = 18.sp, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
