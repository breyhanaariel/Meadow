package com.meadow.core.media.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoPlayer(
    private val context: Context,
    private val playerView: PlayerView
) {
    private var player: ExoPlayer? = null

    fun initialize() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
            playerView.player = player
        }
    }

    suspend fun play(uri: String) = withContext(Dispatchers.Main) {
        initialize()
        player?.apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            play()
        }
    }

    suspend fun pause() = withContext(Dispatchers.Main) {
        player?.pause()
    }

    fun release() {
        player?.release()
        player = null
    }
}