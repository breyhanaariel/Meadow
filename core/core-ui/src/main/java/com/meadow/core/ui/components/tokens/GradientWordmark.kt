package com.meadow.core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import com.meadow.core.ui.theme.LocalWordmarkGradient

@Composable
fun GradientWordmark(
    @DrawableRes resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val gradient = LocalWordmarkGradient.current
    val vector = ImageVector.vectorResource(resId)
    val painter = rememberVectorPainter(image = vector)

    androidx.compose.foundation.Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .graphicsLayer {
                alpha = 1f
                compositingStrategy =
                    androidx.compose.ui.graphics.CompositingStrategy.Offscreen
            }
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = gradient,
                        blendMode = BlendMode.SrcIn
                    )
                }
            }
    )
}
