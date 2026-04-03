package com.meadow.core.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.components.tokens.ComponentRoles
import com.meadow.core.ui.theme.pressScale

enum class MeadowCardType { Info, Alert, Content, Debug, Action }

@Composable
fun MeadowCard(
    modifier: Modifier = Modifier,
    type: MeadowCardType = MeadowCardType.Info,
    content: @Composable () -> Unit
) {
    val backgroundColor = when (type) {
        MeadowCardType.Alert ->
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)

        MeadowCardType.Action ->
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)

        MeadowCardType.Debug ->
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)

        MeadowCardType.Content,
        MeadowCardType.Info ->
            MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier.pressScale(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = ComponentRoles.Shape.BubbleSoft
    ) {
        content()
    }
}

@Composable
fun MeadowCardGrid(
    modifier: Modifier = Modifier,
    type: MeadowCardType = MeadowCardType.Content,
    aspectRatio: Float = 1f,
    content: @Composable BoxScope.() -> Unit
) {
    MeadowCard(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        type = type
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}

@Composable
fun MeadowCardGridContainer(
    modifier: Modifier = Modifier,
    minCardWidth: Dp = 90.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    horizontalSpacing: Dp = 16.dp,
    verticalSpacing: Dp = 16.dp,
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = minCardWidth),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        content = content
    )
}

@Composable
fun MeadowCardHeader(
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    title: String,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (iconResId != null) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (trailingContent != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = trailingContent
            )
        }
    }
}

@Composable
fun MeadowCardFooter(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
