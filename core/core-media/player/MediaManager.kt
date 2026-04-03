package com.meadow.core.media.player

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaManager {

    private var exoPlayer: ExoPlayer? = null

    fun getPlayer(context: Context): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context).build().also {
            exoPlayer = it
        }
    }

    suspend fun playVideo(context: Context, url: String, view: PlayerView) =
        withContext(Dispatchers.Main) {
            val player = getPlayer(context)
            player.setMediaItem(androidx.media3.common.MediaItem.fromUri(url))
            view.player = player
            player.prepare()
            player.play()
        }

    suspend fun stop() = withContext(Dispatchers.Main) {
        exoPlayer?.pause()
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
