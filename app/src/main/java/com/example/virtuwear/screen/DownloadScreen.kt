package com.example.virtuwear.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.virtuwear.viewmodel.DownloadViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.example.virtuwear.R


@Composable
fun DownloadScreen(
    navController: NavController,
    garmentType: String,
    imageUrl: String,
    garmentId: Long,
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val modelPhoto by viewModel.modelPhoto.collectAsStateWithLifecycle()
    val outfitPhotos by viewModel.outfitPhotos.collectAsStateWithLifecycle()
    val isDetailsVisible by viewModel.isDetailsVisible.collectAsStateWithLifecycle()
    val fileNameInput = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getLatestPhoto(context, garmentType)
    }
    LaunchedEffect(imageUrl) {
        Log.d("DownloadScreen", "Image URL received: $imageUrl")
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Result", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFEAECF0))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEAECF0)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEAECF0), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Result Disini", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                // dari url
                                Log.d("VTO Result", "Image URL in Download image: $imageUrl")

                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = "Uploaded Image",
                                    modifier = Modifier.size(500.dp)
                                )
                                TextField(
                                    value = fileNameInput.value,
                                    onValueChange = { fileNameInput.value = it },
                                    label = { Text("File Name") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    singleLine = true
                                )


                                Spacer(modifier = Modifier.height(16.dp))
                                TextButton(onClick = { viewModel.toggleDetailsVisibility() }) {
                                    Text(
                                        text = if (isDetailsVisible) "Hide Details" else "Show Details",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    if (isDetailsVisible) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEAECF0))
                                    .padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                modelPhoto?.let {
                                    Image(
                                        painter = rememberAsyncImagePainter(it),
                                        contentDescription = "Model Photo",
                                        modifier = Modifier.size(200.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Model Photo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        items(outfitPhotos.size) { index ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEAECF0))
                                    .padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Image(
                                    painter = rememberAsyncImagePainter(outfitPhotos[index]),
                                    contentDescription = "Outfit Photo ${index + 1}",
                                    modifier = Modifier.size(200.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Outfit Photo ${index + 1}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.downloadPhoto(context, imageUrl, fileNameInput.value, garmentId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "Download Photo", color = Color.White)
                }
            }
        }
    }
}
