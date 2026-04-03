package com.meadow.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image

data class MeadowDrawerItem(
    val id: String,
    val label: String,
    val iconResId: Int,
    val selected: Boolean = false
)

@Composable
fun MeadowDrawerMenu(
    items: List<MeadowDrawerItem>,
    onItemClick: (MeadowDrawerItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            DrawerMenuRow(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun DrawerMenuRow(
    item: MeadowDrawerItem,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (item.selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(item.iconResId),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                colorFilter = null
            )

            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
