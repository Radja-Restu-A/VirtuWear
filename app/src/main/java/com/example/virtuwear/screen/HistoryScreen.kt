package com.example.virtuwear.screen

import DatePickerModal
import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
    var showDatePicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ){
                                    Button(
                                        onClick = {
                                            showDatePicker = true
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black) ,
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text(
                                            text = "Date",
                                            color = Color.White
                                        )
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp),
                                        color = Color.White,
                                        shape = RoundedCornerShape(24.dp),
                                        border = BorderStroke(2.2.dp, Color.Black)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = garments.count{it.resultImg != null}.toString(),
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (showDatePicker){
                                    DatePickerModal(
                                        onDateSelected = { selectedDate ->
                                            if (selectedDate != null) {
                                                coroutineScope.launch {
                                                    historyViewModel.sortingByDate(selectedDate)
                                                }
                                            }
                                    },
                                        onDismiss = {
                                            showDatePicker = false
                                        }
                                    )
                                }
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