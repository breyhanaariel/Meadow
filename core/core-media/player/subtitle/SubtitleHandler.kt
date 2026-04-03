package com.meadow.core.media.player.subtitle

import androidx.media3.common.text.Cue
import androidx.media3.ui.SubtitleView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.meadow.core.media.R

suspend fun loadSubtitles(view: SubtitleView, cues: List<Pair<Double, String>>) =
    withContext(Dispatchers.Main) {

        val formatted = cues.map { (_, text) ->
            Cue.Builder()
                .setText(
                    text.ifBlank {
                        view.context.getString(R.string.media_subtitle_unknown)
                    }
                )
                .build()
        }

        view.setCues(formatted)
    }
