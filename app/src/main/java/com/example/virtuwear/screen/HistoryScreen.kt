package com.example.virtuwear.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.virtuwear.components.HistoryItem
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.viewmodel.HistoryUiState
import com.example.virtuwear.viewmodel.HistoryViewModel
import com.example.virtuwear.viewmodel.LoginViewModel

@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val state by historyViewModel.uiState
    val firebase = loginViewModel.getCurrentUser()
    val uid = firebase?.uid

    LaunchedEffect(uid) {
        if (uid != null) {
            historyViewModel.getAllGarmentByUser(uid)
        }
    }

    when (state) {
        is HistoryUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is HistoryUiState.Success -> {
            val garments = (state as HistoryUiState.Success).garments
            val leftColumn = garments.filterIndexed { index, _ -> index % 2 == 0 }
            val rightColumn = garments.filterIndexed { index, _ -> index % 2 != 0 }

            // Scrollable Row wrapper (vertically)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // enable scrolling!
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        leftColumn.forEach { img ->
                            img.resultImg?.let { HistoryItem(it) }
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rightColumn.forEach { img ->
                            img.resultImg?.let { HistoryItem(it) }
                        }
                    }
                }
            }
        }

        is HistoryUiState.Error -> {
            val error = (state as HistoryUiState.Error).message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Terjadi kesalahan: $error", color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}




