package com.example.virtuwear.components
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.virtuwear.viewmodel.GarmentViewModel

@Composable
fun BookmarkButton(
    id: Long,
    isSingle: Boolean,
    viewModel: GarmentViewModel,
    size: Dp = 48.dp
) {

    LaunchedEffect(Unit) {
        viewModel.loadBookmarkedItems()
    }

    val bookmarkedSingleItems by viewModel.bookmarkedSingleItems.collectAsState()
    val bookmarkedDoubleItems by viewModel.bookmarkedDoubleItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isBookmarked = if (isSingle) {
        bookmarkedSingleItems.contains(id)
    } else {
        bookmarkedDoubleItems.contains(id)
    }

    Log.d("BookmarkButton", "Is Single: $isSingle Single id: $id isBookmarked: $isBookmarked")

    Box(
        modifier = Modifier
            .size(size) // Gunakan parameter size
            .background(Color.Black, CircleShape) // Background bulat
            .clip(CircleShape)
            .clickable(enabled = !isLoading) {
                if (isSingle) {
                    viewModel.updateSingleBookmark(id, !isBookmarked)
                } else {
                    viewModel.updateDoubleBookmark(id, !isBookmarked)
                }
            }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(size / 2), // Progress indicator 50% dari ukuran tombol
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(size / 2), // Icon 50% dari ukuran tombol
                imageVector = if (isBookmarked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isBookmarked) "Bookmarked" else "Not Bookmarked",
                tint = if (isBookmarked) Color.Red else Color.White
            )
        }
    }

}
