package com.meadow.core.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.IntSize
import kotlin.math.sin
import kotlin.random.Random
import com.meadow.core.ui.R

@Composable
fun Modifier.drawPngFull(pngRes: Int): Modifier {
    val context = LocalContext.current
    val imageBitmap = remember(pngRes) {
        BitmapFactory.decodeResource(context.resources, pngRes)?.asImageBitmap()
    }

    return this.then(
        drawBehind {
            imageBitmap?.let { bmp ->
                drawImage(
                    image = bmp,
                    topLeft = Offset.Zero
                )
            }
        }
    )
}
data class EffectSettings(
    val enablePressEffects: Boolean = true,
    val enableSoftGlow: Boolean = false,
    val enableGlitter: Boolean = false,
    val enablePetals: Boolean = false,
    val enableFloatingParticles: Boolean = false,
    val fieldCornerStyle: Int = 1,
    val fieldFillStyle: Int = 1
)

fun Modifier.pressScale(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    pressedScale: Float = 0.96f,
    duration: Int = 100
): Modifier = composed {

    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            pressed = interaction is PressInteraction.Press
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(duration),
        label = "press_scale"
    )

    this.scale(scale)
}


@Composable
fun MeadowVisualEffects(
    settings: EffectSettings,
    modifier: Modifier = Modifier,
    background: Brush? = null,
    content: @Composable () -> Unit
) {
    val bgModifier = if (background != null)
        modifier.background(background)
    else
        modifier.background(Color.Transparent)

    Box(
        modifier = bgModifier.fillMaxSize()
    ) {
        if (settings.enableSoftGlow) SoftGlowOverlay()
        if (settings.enableGlitter) GlitterOverlay()
        if (settings.enablePetals) FloatingPetalsOverlay()
        if (settings.enableFloatingParticles) FloatingParticlesOverlay(type = ParticleType.Bubble)

        content()
    }
}

@Composable
private fun SoftGlowOverlay(
    modifier: Modifier = Modifier,
    color: Color = MeadowBrand.Lavender200
) {
    Canvas(modifier.fillMaxSize()) {
        val radius = size.minDimension * 0.6f
        drawCircle(
            color = color.copy(alpha = 0.35f),
            radius = radius,
            center = center
        )
    }
}

private data class GlitterPoint(val x: Float, val y: Float)

@Composable
private fun GlitterOverlay(
    modifier: Modifier = Modifier,
    color: Color = MeadowBrand.Blush,
    count: Int = 22
) {
    val infinite = rememberInfiniteTransition(label = "glitter")
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitter_pulse"
    )

    val points = remember(count) {
        List(count) { GlitterPoint(Random.nextFloat(), Random.nextFloat()) }
    }

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        points.forEachIndexed { index, p ->
            val r = (1.5f + (index % 3)) + 2f * pulse
            drawCircle(
                color = color.copy(alpha = 0.4f + 0.3f * pulse),
                radius = r,
                center = Offset(p.x * w, p.y * h)
            )
        }
    }
}

@Composable
private fun FloatingPetalsOverlay(
    modifier: Modifier = Modifier,
    color: Color = MeadowBrand.Peach,
    count: Int = 12
) {
    val infinite = rememberInfiniteTransition(label = "petals")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "petals_time"
    )

    val seeds = remember(count) {
        List(count) {
            val x = Random.nextFloat()
            val speed = 0.3f + Random.nextFloat() * 0.7f
            x to speed
        }
    }

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        seeds.forEachIndexed { index, (xSeed, speed) ->
            val baseY = (t * h * speed + h * index / count) % h
            val sway = 20f * sin((t * 6.28f + index) * 0.8f)
            drawCircle(
                color = color.copy(alpha = 0.55f),
                radius = 6f,
                center = Offset(xSeed * w + sway, baseY)
            )
        }
    }
}

enum class ParticleType(val resId: Int, val defaultSize: Float) {
    Bubble(R.drawable.particle_bubble, 10f),
    Candy(R.drawable.particle_candy, 12f),
    Leaf(R.drawable.particle_leaf, 14f),
    Petal(R.drawable.particle_petal, 10f),
    Snowflake(R.drawable.particle_snowflake, 8f)
}

private data class ParticleSeed(val x: Float, val speed: Float, val drift: Float)

@Composable
fun FloatingParticlesOverlay(
    type: ParticleType,
    count: Int = 18,
    speedMin: Float = 0.3f,
    speedMax: Float = 1.2f,
    alpha: Float = 0.55f,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "floating_particles")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    val seeds = remember(count) {
        List(count) {
            ParticleSeed(
                x = Random.nextFloat(),
                speed = speedMin + Random.nextFloat() * (speedMax - speedMin),
                drift = Random.nextFloat() * 10f
            )
        }
    }

    val image = ImageBitmap.imageResource(type.resId)

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        seeds.forEachIndexed { index, seed ->
            val y = (t * h * seed.speed + h * index / count) % h
            val sway = 20f * sin((t * 6.28f + seed.drift))
            drawImage(
                image = image,
                topLeft = Offset(seed.x * w + sway, y),
                alpha = alpha
            )
        }
    }
}

@Composable
fun SparkleFrameOverlay(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .drawPngFull(R.drawable.frame_sparkle)
) {}

@Composable
fun GlitterFrameOverlay(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .drawPngFull(R.drawable.frame_glitter)
) {}

@Composable
fun SoftGlowOverlayPNG(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .drawPngFull(R.drawable.particle_soft_glow)
) {}

@Composable
fun GlitterParticlesPNG(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .drawPngFull(R.drawable.particle_glitter)
) {}

@Composable
fun PetalParticlesPNG(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .drawPngFull(R.drawable.particle_petal)
) {}

object EffectRoles {
    val IdleBackground = MeadowBrand.Cream
    val Attention = MeadowBrand.Blush
    val Success = MeadowBrand.Success
    val Warning = MeadowBrand.Warning
    val Error = MeadowBrand.Error
}

@Composable
fun EffectFloatingSoft() {
    FloatingParticlesOverlay(type = ParticleType.Bubble)
}

@Composable
fun EffectFloatingHappy() {
    FloatingParticlesOverlay(type = ParticleType.Candy)
}

@Composable
fun EffectFloatingNature() {
    FloatingParticlesOverlay(type = ParticleType.Leaf)
}

@Composable
fun EffectFloatingWinter() {
    FloatingParticlesOverlay(type = ParticleType.Snowflake, speedMax = 0.5f)
}