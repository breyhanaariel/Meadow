package com.meadow.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Controls how MeadowFab is rendered.
 */
sealed class MeadowFabStyle {
    data object Material : MeadowFabStyle()     // default FAB (icon / icon+label)
    data object ImageInShape : MeadowFabStyle() // PNG clipped to FAB shape
    data object ImageOnly : MeadowFabStyle()    // PNG only (no FAB, no clipping)
}

@Composable
fun MeadowFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MeadowFabStyle = MeadowFabStyle.Material,
    icon: ImageVector? = Icons.Filled.Add,
    painterResId: Int? = null,
    label: String? = null,
    visible: Boolean = true,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundResId: Int? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    if (!visible) return

    AnimatedVisibility(visible = visible) {

        when (style) {

            /**
             * STANDARD MATERIAL FAB
             * icon / icon+label / text
             */
            MeadowFabStyle.Material -> {
                FloatingActionButton(
                    onClick = onClick,
                    modifier = modifier,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    shape = CircleShape
                ) {
                    FabContainer(
                        icon = icon,
                        painterResId = painterResId,
                        label = label,
                        backgroundResId = backgroundResId,
                        contentPadding = contentPadding
                    )
                }
            }

            /**
             * PNG INSIDE SHAPE (clipped)
             */
            MeadowFabStyle.ImageInShape -> {
                FloatingActionButton(
                    onClick = onClick,
                    modifier = modifier,
                    containerColor = containerColor,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                painterResId
                                    ?: error("painterResId required for ImageInShape")
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }

            /**
             * PNG ONLY (⭐ no clipping, no FAB)
             */
            MeadowFabStyle.ImageOnly -> {
                Box(
                    modifier = modifier
                        .size(64.dp)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            painterResId
                                ?: error("painterResId required for ImageOnly")
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
private fun FabContainer(
    icon: ImageVector?,
    painterResId: Int?,
    label: String?,
    backgroundResId: Int?,
    contentPadding: PaddingValues
) {
    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        backgroundResId?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = Color.Unspecified
            )
        }

        FabContent(
            icon = icon,
            painterResId = painterResId,
            label = label,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun FabContent(
    icon: ImageVector?,
    painterResId: Int?,
    label: String?,
    contentPadding: PaddingValues
) {
    when {
        painterResId != null && label != null -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(painterResId),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        painterResId != null -> {
            Icon(
                painter = painterResource(painterResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        icon != null && label != null -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        icon != null -> {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        label != null -> {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        else -> {
            Text(
                text = "+",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
