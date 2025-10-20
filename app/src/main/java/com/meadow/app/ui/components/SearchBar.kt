package com.meadow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue

/**
 * SearchBar.kt
 *
 * A reusable pastel search bar component.
 * Used on HomeScreen (for filtering projects) and CatalogScreen.
 */

@Composable
fun MeadowSearchBar(
    hint: String = "Search...",
    onSearch: (String) -> Unit
) {
    val textState = remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8E9FF),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFA06CD5)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = textState.value,
                onValueChange = {
                    textState.value = it
                    onSearch(it.text)
                },
                placeholder = {
                    Text(
                        hint,
                        color = Color(0xFFB69DD8),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color(0xFF7A4AA8)
                ),
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp
                )
            )
        }
    }
}
