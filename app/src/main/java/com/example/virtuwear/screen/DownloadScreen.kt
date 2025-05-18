package com.example.virtuwear.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.virtuwear.viewmodel.DownloadViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.virtuwear.R
import com.example.virtuwear.components.BookmarkButton
import com.example.virtuwear.viewmodel.GarmentViewModel
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.saveable.rememberSaveable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.virtuwear.components.Notes


@Composable
fun DownloadScreen(
    navController: NavController,
    garmentType: String,
    id: Long,
    viewModel: DownloadViewModel = hiltViewModel(),
    garmentViewModel: GarmentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val modelPhoto by viewModel.modelPhoto.collectAsStateWithLifecycle()
    val outfitPhotos by viewModel.outfitPhotos.collectAsStateWithLifecycle()
    val isDetailsVisible by viewModel.isDetailsVisible.collectAsStateWithLifecycle()
    val outfitNameInput = remember { mutableStateOf("") }
    val notesInput = rememberSaveable { mutableStateOf("") }
    var isModelImageLoading by remember { mutableStateOf(true) }
    var isGarmentImageLoading by remember { mutableStateOf(true) }
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fix_loading))

    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getLatestPhoto(context, garmentType)
    }
    LaunchedEffect(id) {
        Log.d("DownloadScreen", "id received: $id")
    }

    LaunchedEffect(Unit) {
        viewModel.getResultById(id)
    }

    LaunchedEffect(Unit) {
        viewModel.updateNotes(id, notesInput)
    }

    LaunchedEffect(Unit) {
        viewModel.updateOutfitName(id, outfitNameInput)
    }
    val response = viewModel.singleResponse.observeAsState()
    Log.d("Download Screen", "Isi response: $response")


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Masukan nama outfit") },
            text = {
                TextField(
                    value = outfitNameInput.value,
                    onValueChange = { outfitNameInput.value = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateOutfitName(id, outfitNameInput)
                    showDialog.value = false
                }) {
                    Text("OK")
                }
            }
        )
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
                                Spacer(modifier = Modifier.height(16.dp))
                                // dari url
                                Log.d("VTO Result", "Image URL in Download image: $id")
                                if (response.value?.isSuccessful == true) {
                                    val singleGarment = response.value?.body()
                                    singleGarment?.let {
                                        var isResultImageLoading by remember { mutableStateOf(true) }

                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.size(500.dp)
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = it.resultImage,
                                                    onState = { state ->
                                                        isResultImageLoading = state !is AsyncImagePainter.State.Success
                                                    }
                                                ),
                                                contentDescription = "Uploaded Image",
                                                modifier = Modifier
                                                    .size(500.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                            )

                                            if (isResultImageLoading && lottieComposition != null) {
                                                LottieAnimation(
                                                    composition = lottieComposition,
                                                    iterations = Int.MAX_VALUE,
                                                    modifier = Modifier.size(500.dp)
                                                )
                                            }
                                        }
                                    }
                                } else if (response.value != null) {
                                    Text("Failed to fetch data. Error: ${response.value?.code()}")
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(500.dp)
                                    ) {
                                        if (lottieComposition != null) {
                                            LottieAnimation(
                                                composition = lottieComposition,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier.size(500.dp)
                                            )
                                        } else {
                                            Text("Loading...")
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = if (outfitNameInput.value.isNotEmpty()) outfitNameInput.value else "Add Outfit Name",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        showDialog.value = true
                                    }
                                )

                                //Notes disini
                                Spacer(modifier = Modifier.height(8.dp))
                                Notes(
                                    value = notesInput.value,
                                    onValueChange = { notesInput.value = it },
                                    modifier = Modifier
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
                                if (response.value?.isSuccessful == true) {
                                    val singleGarment = response.value?.body()
                                    singleGarment?.let {
                                        Log.d("modelImg", "Model yang diambil: ${it.modelImage}")

                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.size(200.dp)
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = it.modelImage.toString(),
                                                    onState = { state ->
                                                        isModelImageLoading = state !is AsyncImagePainter.State.Success
                                                    }
                                                ),
                                                contentDescription = "Model Photo",
                                                modifier = Modifier.size(200.dp)
                                            )

                                            if (isModelImageLoading && lottieComposition != null) {
                                                LottieAnimation(
                                                    composition = lottieComposition,
                                                    iterations = Int.MAX_VALUE,
                                                    modifier = Modifier.size(500.dp)
                                                )
                                            }
                                        }
                                    }
                                } else if (response.value != null) {
                                    Text("Failed to fetch data. Error: ${response.value?.code()}")
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(200.dp)
                                    ) {
                                        if (lottieComposition != null) {
                                            LottieAnimation(
                                                composition = lottieComposition,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier.size(500.dp)
                                            )
                                        } else {
                                            Text("Loading...")
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Model Photo",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        val outfitPhotos =
                            response.value?.body()?.garmentImage?.split(",") ?: emptyList()


                        items(outfitPhotos.size) { index ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEAECF0))
                                    .padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                // Logika untuk menampilkan gambar berdasarkan jumlah outfit
                                if (outfitPhotos.size == 1) {
                                    // Kasus: hanya ada satu outfit
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(200.dp)
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = outfitPhotos[0],
                                                onState = { state ->
                                                    isGarmentImageLoading = state !is AsyncImagePainter.State.Success
                                                }
                                            ),
                                            contentDescription = "Outfit Photo 1",
                                            modifier = Modifier.size(200.dp)
                                        )

                                        if (isGarmentImageLoading && lottieComposition != null) {
                                            LottieAnimation(
                                                composition = lottieComposition,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier.size(500.dp)
                                            )
                                        }
                                    }
                                } else if (outfitPhotos.size > 1) {
                                    // Kasus: lebih dari satu outfit
                                    var isCurrentOutfitLoading by remember { mutableStateOf(true) }
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(200.dp)
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = outfitPhotos[index],
                                                onState = { state ->
                                                    isCurrentOutfitLoading = state !is AsyncImagePainter.State.Success
                                                }
                                            ),
                                            contentDescription = "Outfit Photo ${index + 1}",
                                            modifier = Modifier.size(200.dp)
                                        )

                                        if (isCurrentOutfitLoading && lottieComposition != null) {
                                            LottieAnimation(
                                                composition = lottieComposition,
                                                iterations = Int.MAX_VALUE,
                                                modifier = Modifier.size(500.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Text(text = "No outfit photos available.")
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Outfit Photo ${index + 1}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (response.value?.isSuccessful == true) {
                            val singleGarment = response.value?.body()
                            singleGarment?.let {
                                viewModel.downloadPhoto(
                                    context,
                                    it.resultImage,
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Download Photo",
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.download_svg),
                            contentDescription = "Download Icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                BookmarkButton(
                    id = id,
                    isSingle = true,
                    viewModel = garmentViewModel,
                    size = 50.dp
                )
            }
        }
    }
}