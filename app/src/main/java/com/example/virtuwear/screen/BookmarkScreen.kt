package com.example.virtuwear.screen


import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.virtuwear.components.BookmarkButton
import com.example.virtuwear.components.HistoryItem
import com.example.virtuwear.components.Search
import com.example.virtuwear.viewmodel.HistoryUiState
import com.example.virtuwear.viewmodel.LoginViewModel
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.viewmodel.BookmarkUiState
import com.example.virtuwear.viewmodel.BookmarkViewModel
import com.example.virtuwear.viewmodel.GarmentViewModel

@Composable
fun BookmarkScreen(
    navController: NavController,
    onDismiss: () -> Unit,
    bookmarkViewModel : BookmarkViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    garmentViewModel: GarmentViewModel = hiltViewModel()
) {
    val state by bookmarkViewModel.uiState
    val firebase = loginViewModel.getCurrentUser()
    val uid = firebase?.uid
    val selectedGarment by bookmarkViewModel.selectedGarment.collectAsState()

    LaunchedEffect(uid) {
        if (uid != null) {
            bookmarkViewModel.getBookmarkedGarment(uid)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = "Bookmark",
            onBackClick = { onDismiss }
        )

        when (state) {
            is BookmarkUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BookmarkUiState.Success -> {
                val garments = (state as BookmarkUiState.Success).garments
                if (garments.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Outfit yang kamu cari tidak ada.")
                    }
                } else {
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
                                for (i in garments.indices.filter { it % 2 == 0 }) {
                                    garments[i].resultImage?.let { imageUrl ->
                                        HistoryItem(imageUrl, onClick = {
                                            bookmarkViewModel.selectGarment(garments[i])
                                            navController.navigate("garmentDetail/${garments[i].id}")
                                        },
                                            bookmarkButton = {
                                                garments[i].id?.let {
                                                    BookmarkButton(
                                                        id = it,
                                                        isSingle = true,
                                                        viewModel = garmentViewModel
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (i in garments.indices.filter { it % 2 == 1 }) {
                                    garments[i].resultImage?.let { imageUrl ->
                                        HistoryItem(imageUrl, onClick = {
                                            bookmarkViewModel.selectGarment(garments[i])
                                            navController.navigate("garmentDetail/${garments[i].id}")
                                        },
                                            bookmarkButton = {
                                                garments[i].id?.let {
                                                    BookmarkButton(
                                                        id = it,
                                                        isSingle = true,
                                                        viewModel = garmentViewModel
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is BookmarkUiState.Error -> {
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

@Composable
fun TopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Text(
            text = title,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

