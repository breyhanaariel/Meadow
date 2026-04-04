package com.meadow.app.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.meadow.app.ui.navigation.AppRoutes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(1500)
        navController.navigate(AppRoutes.HOME) {
            popUpTo(AppRoutes.SPLASH) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize())
}