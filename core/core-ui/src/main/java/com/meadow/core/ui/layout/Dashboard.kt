package com.meadow.core.ui.layout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


/* ─── MEADOW DASHBOARD ───────────────────── */

@Composable
fun MeadowDashboard(
    tabs: List<String>,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    content: @Composable (selectedTab: Int) -> Unit
) {

    val selectedTabState = remember { mutableIntStateOf(0) }
    val selectedTab = selectedTabState.intValue

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        /* ─── HEADER SLOT ───────────────────── */
        header()

        /* ─── TAB ROW ───────────────────── */
        TabRow(
            selectedTabIndex = selectedTab,
            indicator = { tabPositions ->
                if (selectedTab in tabPositions.indices) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab])
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTabState.intValue = index },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }

        /* ─── TAB CONTENT SURFACE ───────────────────── */
        MeadowDashboardSurface {
            content(selectedTab)
        }
    }
}
@Composable
fun MeadowDashboardSurface(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}
@Composable
fun MeadowDashboardSlider(
    pageCount: Int,
    icon: Int,
    modifier: Modifier = Modifier,
    activeSize: Dp = 80.dp,
    inactiveSize: Dp = 50.dp,
    content: @Composable (page: Int) -> Unit
) {

    val pagerState = rememberPagerState { pageCount }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        MeadowSliderIndicator(
            icon = icon,
            currentPage = pagerState.currentPage,
            pageCount = pageCount
        ) { page ->
            scope.launch {
                pagerState.animateScrollToPage(page)
            }
        }

        HorizontalPager(
            state = pagerState
        ) { page ->
            content(page)
        }

        MeadowSliderIndicator(
            icon = icon,
            currentPage = pagerState.currentPage,
            pageCount = pageCount
        ) { page ->
            scope.launch {
                pagerState.animateScrollToPage(page)
            }
        }
    }
}

@Composable
private fun MeadowSliderIndicator(
    icon: Int,
    currentPage: Int,
    pageCount: Int,
    onSelectPage: (Int) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        repeat(pageCount) { index ->

            val isActive = index == currentPage

            val size by animateDpAsState(
                targetValue = if (isActive) 80.dp else 50.dp,
                label = "sliderSize"
            )

            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(size)
                    .padding(horizontal = 6.dp)
                    .clickable {
                        onSelectPage(index)
                    }
            )
        }
    }
}