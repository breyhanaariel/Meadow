package com.meadow.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meadow.R
import com.meadow.app.ui.theme.PastelLavender

/**
 * EmptyState.kt
 *
 * Shown when there’s no data (no projects, scripts, etc.)
 * Adds a friendly unicorn or sparkle image and message.
 */
@Composable
fun EmptyState(message: String = "Nothing here yet!") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_unicorn),
            contentDescription = "Empty State Unicorn",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = PastelLavender
        )
    }
}
