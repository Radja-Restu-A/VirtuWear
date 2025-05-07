package com.example.virtuwear.screen

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.virtuwear.components.HistoryItem
import com.example.virtuwear.components.Search
import com.example.virtuwear.viewmodel.HistoryUiState
import com.example.virtuwear.viewmodel.HistoryViewModel
import com.example.virtuwear.viewmodel.LoginViewModel

@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val state by historyViewModel.uiState
    val firebase = loginViewModel.getCurrentUser()
    val uid = firebase?.uid
    var query by remember { mutableStateOf("") }

    LaunchedEffect(uid) {
        if (uid != null) {
            historyViewModel.getAllGarmentByUser(uid)
        }
    }

    Column {
        // Header search bar
        Search(
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                historyViewModel.searchGarment(query)
            }
        )

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
                        Text("You still have not generated any photos.")
                    }
                } else {
                    // Create two equal columns using a better approach
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
                            // Left column
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (i in garments.indices.filter { it % 2 == 0 }) {
                                    garments[i].resultImg?.let { imageUrl ->
                                        HistoryItem(imageUrl, onClick = {
                                            historyViewModel.selectGarment(garments[i])
                                            navController.navigate("garmentDetail/${garments[i].idSingle}")
                                        })
                                    }
                                }
                            }

                            // Right column
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Take items at odd indices (1, 3, 5, ...)
                                for (i in garments.indices.filter { it % 2 == 1 }) {
                                    garments[i].resultImg?.let { imageUrl ->
                                        HistoryItem(imageUrl, onClick = {
                                            historyViewModel.selectGarment(garments[i])
                                            navController.navigate("garmentDetail/${garments[i].idSingle}")
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is HistoryUiState.Error -> {
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
}