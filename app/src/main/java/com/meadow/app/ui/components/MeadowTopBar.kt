package com.meadow.app.ui.components

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meadow.app.R
import com.meadow.core.ui.locale.AppLanguage
import com.meadow.core.ui.locale.LanguageFlagResolver
import com.meadow.core.ui.theme.ThemeIconResolver
import com.meadow.core.ui.theme.ThemeViewModel
import com.meadow.feature.common.state.KebabAction
import androidx.compose.animation.core.animateFloatAsState

private val TopBarHeight = 64.dp
private val FlagSize = 44.dp
private val WordmarkHeight = 48.dp
private val SquareSize = 48.dp
private val KebabWidth = 20.dp
private val KebabHeight = 48.dp

@Composable
fun MeadowTopBar(
    onWordmarkClick: () -> Unit,
    onMenuClick: () -> Unit,
    onChatClick: () -> Unit,
    onSettingsClick: () -> Unit,
    kebabActions: List<KebabAction>,
    currentLanguage: AppLanguage,
    onLanguageClick: () -> Unit
) {
    val themeViewModel: ThemeViewModel = viewModel()
    val theme by themeViewModel.theme.collectAsState()
    val hasKebab = kebabActions.isNotEmpty()
    val wordmarkScale by animateFloatAsState(
        targetValue = if (hasKebab) 1.0f else 1.25f,
        label = "wordmarkScale"
    )
    var kebabExpanded by remember { mutableStateOf(false) }

    /* ─── FIXED TOP BAR ─── */
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(TopBarHeight)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        /* ─── LEFT: FLAG + WORDMARK ─── */
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(LanguageFlagResolver.flagFor(currentLanguage)),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(0.82f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onLanguageClick() }
            )

            Spacer(Modifier.width(8.dp))

            Image(
                painter = painterResource(ThemeIconResolver.wordmark(theme)),
                contentDescription = stringResource(R.string.cd_home),
                modifier = Modifier
                    .height(WordmarkHeight)
                    .graphicsLayer {
                        scaleX = wordmarkScale
                        scaleY = wordmarkScale
                    }
                    .clickable { onWordmarkClick() },
                colorFilter = null
            )
        }

        /* ─── RIGHT: ICONS ─── */
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopBarSquare(
                resId = ThemeIconResolver.planet(theme),
                size = SquareSize,
                contentScale = 0.95f,
                onClick = onMenuClick
            )
            TopBarSquare(
                resId = ThemeIconResolver.chat(theme),
                size = SquareSize,
                onClick = onChatClick
            )
            TopBarSquare(
                resId = ThemeIconResolver.crescent(theme),
                size = SquareSize,
                onClick = onSettingsClick
            )
            if (kebabActions.isNotEmpty()) {
                Box {
                    TopBarKebab(
                        resId = ThemeIconResolver.kebabmenu(theme),
                        onClick = { kebabExpanded = true }
                    )

                    DropdownMenu(
                        expanded = kebabExpanded,
                        onDismissRequest = { kebabExpanded = false }
                    ) {
                        kebabActions.forEach { action ->
                            DropdownMenuItem(
                                text = { Text(action.label) },
                                onClick = {
                                    kebabExpanded = false
                                    action.onClick()
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}


@Composable
private fun TopBarSquare(
    resId: Int,
    size: Dp,
    contentScale: Float = 0.68f,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(size),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(resId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(0.68f)
                    .graphicsLayer {
                        scaleX = contentScale / 0.68f
                        scaleY = contentScale / 0.68f
                    },
                colorFilter = null
            )
        }
    }
}


@Composable
private fun TopBarKebab(
    resId: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(KebabWidth)
            .height(KebabHeight),
        shape = MaterialTheme.shapes.extraLarge, // pill-like
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(resId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .aspectRatio(1f),
                colorFilter = null
            )
        }
    }
}
