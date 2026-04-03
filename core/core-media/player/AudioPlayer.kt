package com.meadow.core.media.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioPlayer(private val context: Context) {
    private var player: ExoPlayer? = null

    fun initialize() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
    }

    suspend fun play(url: String) = withContext(Dispatchers.Main) {
        initialize()
        player?.apply {
            setMediaItem(MediaItem.fromUri(url))
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