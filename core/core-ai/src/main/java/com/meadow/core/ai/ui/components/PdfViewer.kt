package com.meadow.core.ai.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import com.meadow.core.ai.R
import com.meadow.core.ai.domain.model.Bookmark
import com.meadow.core.ai.domain.model.PdfSearchResult
import com.meadow.core.ai.domain.model.PdfState
import com.meadow.core.media.viewer.PdfPageRenderer
import com.meadow.core.ui.R as CoreUiR
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
fun PdfViewer(
    state: PdfState,
    onSearch: (String) -> Unit,
    onAddBookmark: (Int) -> Unit,
    onOpenBookmark: (Bookmark) -> Unit,
    onLinkCatalog: (String) -> Unit,
    onDismissDialog: () -> Unit = {},
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    var showMiniDrawer by remember { mutableStateOf(false) }

    var scale by remember { mutableStateOf(1f) }
    val transformState = remember {
        TransformableState { zoomChange, _, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 4f)
        }
    }

    LaunchedEffect(state.currentPage) {
        if (state.currentPage > 0) {
            scrollState.scrollToItem(state.currentPage - 1)
        }
    }

    val renderer = remember(state.documentPath) {
        try { PdfPageRenderer(context, state.documentPath) }
        catch (e: Exception) { null }
    } ?: return

    val totalPages = renderer.pageCount

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        Column {

            /* ─── Top Bar ───────────────── */

            Surface(
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(CoreUiR.string.action_back),
                        modifier = Modifier.clickable { onBack() },
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.weight(1f))

                    IconButton(onClick = { showMiniDrawer = !showMiniDrawer }) {
                        Icon(Icons.Default.Menu, null)
                    }

                    IconButton(onClick = {
                        val page =
                            (scrollState.firstVisibleItemIndex + 1)
                                .coerceIn(1, totalPages)
                        onAddBookmark(page)
                    }) {
                        Icon(Icons.Default.Bookmark, null)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            /* ─── Search ───────────────── */

            OutlinedTextField(
                value = state.query,
                onValueChange = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp)),
                label = { Text(stringResource(CoreUiR.string.search_notes)) }
            )

            Spacer(Modifier.height(8.dp))

            /* ─── Page Viewer ───────────────── */

            Box(Modifier.weight(1f)) {

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
                ) {

                    items(totalPages) { pageIndex ->

                        val bitmap = remember(pageIndex) {
                            renderer.renderPage(pageIndex)
                        }

                        if (bitmap != null) {

                            Box(Modifier.padding(vertical = 12.dp)) {

                                Image(
                                    bitmap = bitmap,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale
                                        )
                                        .transformable(transformState)
                                )

                                if (state.searchResults.isNotEmpty()) {
                                    HighlightOverlay(
                                        pageIndex = pageIndex,
                                        results = state.searchResults,
                                        highlightColor = MaterialTheme.colorScheme.secondary,
                                        bitmapWidth = bitmap.width.toFloat(),
                                        bitmapHeight = bitmap.height.toFloat()
                                    )
                                }
                            }
                        }
                    }
                }

                /* ─── Vertical Slider ───────────────── */

                Slider(
                    value = scrollState.firstVisibleItemIndex.toFloat(),
                    onValueChange = { page ->
                        scope.launch {
                            scrollState.scrollToItem(page.roundToInt())
                        }
                    },
                    valueRange = 0f..(totalPages - 1).toFloat(),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(280.dp)
                        .rotate(270f)
                )
            }

            /* ─── Thumbnails ───────────────── */

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {

                items(totalPages) { index ->

                    val thumbnail = remember(index) {
                        renderer.renderThumbnail(index)
                    }

                    Box(
                        Modifier.clickable {
                            scope.launch { scrollState.scrollToItem(index) }
                        }
                    ) {

                        if (thumbnail != null) {
                            Image(
                                bitmap = thumbnail,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }

                        if (state.bookmarks.any { it.pageNumber == index + 1 }) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .align(Alignment.TopEnd)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }

        /* ─── Mini Drawer ───────────────── */

        AnimatedVisibility(
            visible = showMiniDrawer,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            Surface(
                tonalElevation = 6.dp,
                shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                modifier = Modifier.fillMaxHeight().width(200.dp)
            ) {
                LazyColumn(Modifier.padding(12.dp)) {
                    items(totalPages) { page ->
                        Text(
                            "Page ${page + 1}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        scrollState.scrollToItem(page)
                                        showMiniDrawer = false
                                    }
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }

}
@Composable
private fun HighlightOverlay(
    pageIndex: Int,
    results: List<PdfSearchResult>,
    highlightColor: Color,
    bitmapWidth: Float,
    bitmapHeight: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {

        results
            .filter { it.pageIndex == pageIndex }
            .forEach { result ->

                drawRect(
                    color = highlightColor.copy(alpha = 0.35f),
                    topLeft = Offset(result.rect.left, result.rect.top),
                    size = Size(result.rect.width, result.rect.height)
                )
            }
    }
}