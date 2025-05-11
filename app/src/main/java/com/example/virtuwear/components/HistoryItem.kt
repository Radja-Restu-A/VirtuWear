package com.example.virtuwear.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun HistoryItem(
    resultImg: String,
    onClick: (() -> Unit)?,
    bookmarkButton: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (onClick != null) {
                    onClick ()
                }
            }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            AsyncImage(
                model = resultImg,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (bookmarkButton != null) {
            Box(
                modifier = Modifier.padding(8.dp)
                    .align (Alignment.TopStart)
            ) {
                bookmarkButton()
            }
        }
    }
}