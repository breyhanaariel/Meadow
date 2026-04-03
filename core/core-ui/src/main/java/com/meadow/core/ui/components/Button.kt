package com.meadow.core.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.meadow.core.ui.components.tokens.ComponentRoles
import com.meadow.core.ui.theme.pressScale

enum class MeadowButtonType { Primary, Secondary, Icon, Nav, Toggle, Ghost }

@Composable
fun MeadowButton(
    text: String,
    type: MeadowButtonType = MeadowButtonType.Primary,
    enabled: Boolean = true,
    textMaxLines: Int = 1,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val buttonColors: ButtonColors =
        when (type) {

            MeadowButtonType.Primary ->
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )

            MeadowButtonType.Secondary ->
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )

            MeadowButtonType.Icon,
            MeadowButtonType.Nav ->
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )

            MeadowButtonType.Toggle ->
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

            MeadowButtonType.Ghost ->
                ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
        }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.pressScale(),
        shape = ComponentRoles.Shape.RoundedMedium,
        colors = buttonColors
    ) {
        Text(
            text = text,
            maxLines = textMaxLines,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
        )
    }
}
