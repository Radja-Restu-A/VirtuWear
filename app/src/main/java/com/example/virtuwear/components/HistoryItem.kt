package com.example.virtuwear.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun HistoryItem(
    resultImg: String,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            if (onClick != null) {
                onClick()
            }
        },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = resultImg,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}