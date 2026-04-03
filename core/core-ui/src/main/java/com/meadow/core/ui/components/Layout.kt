package com.meadow.core.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R
import com.meadow.core.ui.components.tokens.ComponentRoles
import com.meadow.core.ui.theme.MeadowGradients

@Composable
fun MeadowScaffold(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingPanel: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            topBar()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                content = content
            )
            bottomBar()
        }
        floatingPanel()
    }
}

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
        }

        content()
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }

    LaunchedEffect(initiallyExpanded) {
        expanded = initiallyExpanded
    }

    Surface(
        shape = ComponentRoles.Shape.BubbleSoft,
        tonalElevation = ComponentRoles.Elevation.Low,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = if (expanded)
                        stringResource(R.string.arrow_up)
                    else stringResource(R.string.arrow_down)
                )
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    content = content
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> StickyHeaderList(
    sections: Map<String, List<T>>,
    itemContent: @Composable (T) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sections.forEach { (header, items) ->
            stickyHeader {
                Surface(
                    shape = ComponentRoles.Shape.RoundedSmall,
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = ComponentRoles.Elevation.Low
                ) {
                    Text(
                        text = header,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            items(items) { item ->
                itemContent(item)
            }
        }
    }
}

object MeadowLayout {

    @Composable
    fun SidePanel(
        width: Dp = 360.dp,
        onClose: () -> Unit,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClose() }
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            )


            Surface(
                tonalElevation = ComponentRoles.Elevation.Medium,
                shape = ComponentRoles.Shape.RoundedMedium,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width)
                    .align(Alignment.CenterEnd)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    content = content
                )
            }
        }
    }

    @Composable
    fun ColumnScreen(
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun SectionContainer(
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    background: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (background) {
                    Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content()

        if (showDivider) {
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickyHeaderScaffold(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp)
            ) {
                header()
            }
        }

        content()
    }
}