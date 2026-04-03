package com.meadow.app.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.meadow.core.ui.theme.MeadowTheme
import com.meadow.core.ui.theme.MeadowThemeVariant
import com.meadow.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    MeadowTheme {
        var visible by remember { mutableStateOf(false) }
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(1000),
            label = "splash_fade"
        )

        LaunchedEffect(Unit) {
            visible = true
            delay(2000)
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_splash),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
                contentScale = ContentScale.Crop
            )
        }
    }
}
