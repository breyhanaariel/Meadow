package com.meadow.core.ui.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.meadow.core.ui.R
import com.meadow.core.ui.theme.MeadowSounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object VisualUtils {

    @Composable
    fun shimmerAlphaAnim(duration: Int = 1200): Float {
        val infinite = rememberInfiniteTransition(label = "shimmer")
        val alpha by infinite.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alphaAnim"
        )
        return alpha
    }

    @Composable
    fun floatUpDownAnim(range: Float = 4f, duration: Int = 2000): Float {
        val infinite = rememberInfiniteTransition(label = "float")
        val offset by infinite.animateFloat(
            initialValue = -range,
            targetValue = range,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floatOffset"
        )
        return offset
    }

    @Composable
    fun GlitterPulse(modifier: Modifier = Modifier, intensity: Float = 1f): Modifier {
        val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAnim"
        )
        return modifier.graphicsLayer {
            scaleX = pulse * intensity
            scaleY = pulse * intensity
        }
    }

    fun playUiSound(context: Context, soundFileName: String) {
        try {
            val soundResId = when (soundFileName) {
                MeadowSounds.GlitterTap -> R.raw.glitter_tap
                MeadowSounds.SuccessPop -> R.raw.success_pop
                MeadowSounds.SoftChime -> R.raw.soft_chime
                MeadowSounds.PetalSwipe -> R.raw.petal_swipe
                MeadowSounds.CandyDust -> R.raw.candy_dust_swipe
                else -> {
                    Log.w("VisualUtils", "Unknown sound requested: $soundFileName")
                    null
                }
            }

            soundResId?.let {
                val mediaPlayer = MediaPlayer.create(context, it)
                mediaPlayer.setOnCompletionListener { mp -> mp.release() }
                mediaPlayer.start()
            }

        } catch (e: Exception) {
            Log.e("VisualUtils", "Error playing sound: ${e.message}", e)
        }
    }

    fun CoroutineScope.playAsyncSound(context: Context, soundFileName: String) {
        launch { playUiSound(context, soundFileName) }
    }
}