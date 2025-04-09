package com.example.virtuwear.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(garments) { resultImg ->
                    resultImg.resultImg?.let { HistoryItem(it) }
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


@Composable
fun HistoryItem(resultImg: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(), // lebar tetap (karena grid 2 kolom)
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = resultImg,
            contentDescription = null,
            contentScale = ContentScale.FillWidth, // hanya isi lebar, tinggi mengikuti image asli
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
