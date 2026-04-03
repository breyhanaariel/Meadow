package com.meadow.core.media.player

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meadow.core.media.R

@Composable
fun PlayButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(stringResource(R.string.media_play))
    }
}

@Composable
fun PauseButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(stringResource(R.string.media_pause))
    }
}

@Composable
fun StopButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(stringResource(R.string.media_stop))
    }
}