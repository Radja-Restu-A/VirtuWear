package com.example.virtuwear.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.virtuwear.viewmodel.DownloadViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DownloadScreen(
    navController: NavController,
    garmentType: String,
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val modelPhoto by viewModel.modelPhoto.collectAsStateWithLifecycle()
    val outfitPhotos by viewModel.outfitPhotos.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getLatestPhoto(context, garmentType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
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
            Text(text = "Download Photo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFEAECF0), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Result Disini", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (outfitPhotos.isNotEmpty()) {
                            outfitPhotos.take(1).forEach { uri ->
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Outfit Photo 1",
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Outfit Photo 1", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (outfitPhotos.size > 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = rememberAsyncImagePainter(outfitPhotos[1]),
                                contentDescription = "Outfit Photo 2",
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Outfit Photo 2", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    modelPhoto?.let { viewModel.downloadPhoto(context, it) }
                    outfitPhotos.forEach { uri -> viewModel.downloadPhoto(context, uri) }
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