package com.meadow.core.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable fun MeadowSpinner(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable fun MeadowLoadingBar(modifier: Modifier = Modifier) {
    LinearProgressIndicator(modifier = modifier)
}