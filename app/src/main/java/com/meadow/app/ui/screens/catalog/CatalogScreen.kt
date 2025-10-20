package com.meadow.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meadow.app.domain.models.CatalogItem
import com.meadow.app.ui.components.PastelButton
import com.meadow.app.ui.theme.*
import com.meadow.app.viewmodel.CatalogViewModel

/**
 * CatalogScreen.kt
 *
 * Displays catalog items, filters, and quick links to creative tools like Timeline, Wiki, etc.
 * Includes floating “Add Item” button and pastel card design.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    projectId: String,
    catalogViewModel: CatalogViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateToTimeline: (String) -> Unit = {},
    onNavigateToWiki: (String) -> Unit = {},
    onNavigateToMindMap: (String) -> Unit = {},
    onNavigateToFamilyTree: (String) -> Unit = {},
    onNavigateToPlotCards: (String) -> Unit = {}
) {
    val allItems by catalogViewModel.getItemsForProject(projectId).collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") }

    val filteredItems = allItems.filter {
        (filterType == "All" || it.type == filterType) &&
                (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catalog 📖", color = TextPrimary) },
                actions = {
                    IconButton(onClick = { /* TODO: Open Filter Menu */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelPink)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { catalogViewModel.createCatalogItem(projectId) },
                containerColor = PastelLavender,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Catalog Item")
            }
        },
        containerColor = BackgroundLight
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MeadowGradients.LavenderPink)
                .padding(padding)
                .padding(16.dp)
        ) {
            // 🔍 Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search catalog...", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .background(PastelPeach, RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🌷 Filter Chips
            FilterChipsRow(
                selected = filterType,
                onSelect = { filterType = it },
                options = listOf("All", "Character", "World", "Prop", "Wardrobe")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🌼 Creative Tools Cards
            CreativeToolCards(
                onNavigateToTimeline = { onNavigateToTimeline(projectId) },
                onNavigateToWiki = { onNavigateToWiki(projectId) },
                onNavigateToMindMap = { onNavigateToMindMap(projectId) },
                onNavigateToFamilyTree = { onNavigateToFamilyTree(projectId) },
                onNavigateToPlotCards = { onNavigateToPlotCards(projectId) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📋 Catalog Items List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems.size) { index ->
                    CatalogItemCard(filteredItems[index])
                }
            }
        }
    }
}

/**
 * 🌼 Filter Chips Row
 */
@Composable
fun FilterChipsRow(
    selected: String,
    onSelect: (String) -> Unit,
    options: List<String>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        options.forEach { option ->
            val isSelected = selected == option
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(option) },
                label = { Text(option, color = if (isSelected) TextPrimary else TextSecondary) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (isSelected) PastelMint else PastelPeach
                )
            )
        }
    }
}

/**
 * 🌸 Catalog Item Card
 */
@Composable
fun CatalogItemCard(item: CatalogItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable { /* TODO: open item details */ },
        colors = CardDefaults.cardColors(containerColor = PastelBlue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Text(item.type, color = TextSecondary, fontSize = 14.sp)
            if (!item.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.description, color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

/**
 * 🌟 Creative Tool Cards (Wiki, Timeline, etc.)
 */
@Composable
fun CreativeToolCards(
    onNavigateToTimeline: () -> Unit,
    onNavigateToWiki: () -> Unit,
    onNavigateToMindMap: () -> Unit,
    onNavigateToFamilyTree: () -> Unit,
    onNavigateToPlotCards: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ToolCard("📅 Timeline", PastelRose, onNavigateToTimeline)
        ToolCard("📘 Wiki", PastelLavender, onNavigateToWiki)
        ToolCard("🧠 Mind Map", PastelMint, onNavigateToMindMap)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ToolCard("👪 Family Tree", PastelPeach, onNavigateToFamilyTree)
        ToolCard("🗂️ Plot Cards", PastelBlue, onNavigateToPlotCards)
    }
}

/**
 * 🌈 Reusable Creative Tool Card
 */
@Composable
fun ToolCard(
    title: String,
    background: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(120.dp)
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}