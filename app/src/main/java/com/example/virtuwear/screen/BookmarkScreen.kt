package com.example.virtuwear.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.virtuwear.components.GarmentDetail
import com.example.virtuwear.components.HistoryItem
import com.example.virtuwear.viewmodel.HistoryUiState
import com.example.virtuwear.viewmodel.HistoryViewModel
import com.example.virtuwear.viewmodel.LoginViewModel
import com.example.virtuwear.data.model.SingleGarmentModel

@Composable
fun BookmarkScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val state by historyViewModel.uiState
    val firebase = loginViewModel.getCurrentUser()
    val uid = firebase?.uid
    var selectedGarment by remember { mutableStateOf<SingleGarmentModel?>(null) }

    LaunchedEffect(uid) {
        if (uid != null) {
            historyViewModel.getAllGarmentByUser(uid)
        }
    }

    Column {

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
                if (garments.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nothing to show here")
                    }
                } else {
                    val leftColumn = garments.filterIndexed { index, _ -> index % 2 == 0 }
                    val rightColumn = garments.filterIndexed { index, _ -> index % 2 != 0 }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
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
                                    img.resultImg?.let {
                                        HistoryItem(it, onClick = { selectedGarment = img })
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rightColumn.forEach { img ->
                                    img.resultImg?.let {
                                        HistoryItem(it, onClick = { selectedGarment = img })
                                    }
                                }
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
                    Text(
                        text = "The image you are looking for are not available.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
    selectedGarment?.let { garment ->
        GarmentDetail(garment = garment, onDismiss = { selectedGarment = null })
    }
}