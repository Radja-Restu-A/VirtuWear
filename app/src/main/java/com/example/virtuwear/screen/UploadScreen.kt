package com.example.virtuwear.screen

import android.Manifest
import android.content.pm.PackageManager
import android.R
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.virtuwear.components.Alert
import com.example.virtuwear.components.AlertType
import com.example.virtuwear.components.HistoryDialog
import com.example.virtuwear.data.model.DoubleGarmentModel
import com.example.virtuwear.data.model.GarmentDto
import com.example.virtuwear.data.model.ModelDto
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import com.example.virtuwear.repository.UserRepository
import com.example.virtuwear.viewmodel.UploadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.virtuwear.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UploadPhotoScreen(
    navController: NavController,
    uploadViewModel: UploadViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showError by remember { mutableStateOf(false) }
    var permissionError by remember { mutableStateOf("") }
    val selectedGarmentType by uploadViewModel.selectedGarmentType
    val imageUris: List<Uri?> = uploadViewModel.imageUris
    var isLoading by remember { mutableStateOf(false) }
    var showUploadOptions by remember { mutableStateOf(false) }
    var currentUploadIndex by remember { mutableStateOf(0) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var historyType by remember { mutableStateOf("") }

    val firebase = loginViewModel.getCurrentUser()
    val user = firebase?.uid
    val modelList by uploadViewModel.modelList
    val garmentList by uploadViewModel.garmentList

    LaunchedEffect(Unit) {
        user?.let {
            uploadViewModel.getModel(it)
            uploadViewModel.getGarment(it)
        }
    }

    // File Path sementara buat gambar dari camera
    val tempImageFile = remember {
        try {
            File.createTempFile(
                "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_",
                ".jpg",
                context.cacheDir
            )
        } catch (e: Exception) {
            Log.e("UploadScreen", "Error creating temp file: ${e.message}")
            null
        }
    }

    val photoURI = remember {
        tempImageFile?.let {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                it
            )
        }
    }

    // Minta permission sama pengguna buat akses directory sama kamera
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val readGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }

        if (cameraGranted && readGranted) {
            when (currentUploadIndex) {
                0, 1, 2 -> showUploadOptions = true
            }
        } else {
            permissionError = if (!cameraGranted && !readGranted) {
                "Camera and storage permissions are required"
            } else if (!cameraGranted) {
                "Camera permission is required"
            } else {
                "Storage permission is required"
            }
            Toast.makeText(context, permissionError, Toast.LENGTH_LONG).show()
        }
    }

    // Image picker launcher dari gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadViewModel.addImageUris(it, currentUploadIndex)
            showUploadOptions = false
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoURI != null) {
            uploadViewModel.addImageUris(photoURI, currentUploadIndex)
            showUploadOptions = false
        } else if (!success) {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    // Check permissions
    fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            showUploadOptions = true
        }
    }

    fun onUploadBoxClick(index: Int) {
        currentUploadIndex = index
        checkAndRequestPermissions()
    }

    if (showUploadOptions) {
        UploadOptionsDialog(
            onDismiss = { showUploadOptions = false },
            onGalleryClick = {
                imagePickerLauncher.launch("image/*")
            },
            onCameraClick = {
                if (photoURI != null) {
                    cameraLauncher.launch(photoURI)
                } else {
                    Toast.makeText(context, "Unable to create photo file", Toast.LENGTH_SHORT).show()
                }
            },
            onHistoryClick = {
                showUploadOptions = false
                historyType = if (currentUploadIndex == 0) "Model" else "Garment"
                showHistoryDialog = true
            }
        )
    }

    if (showHistoryDialog) {
        HistoryDialog(
            items = if (historyType == "Model") modelList else garmentList,
            itemType = historyType,
            onItemSelected = { uri ->
                uploadViewModel.addImageUris(uri, currentUploadIndex)
            },
            onDismiss = { showHistoryDialog = false }
        )
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
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Upload Photo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFEAECF0), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    UploadBox(
                        imageUri = imageUris.getOrNull(0),
                        onUploadClick = { onUploadBoxClick(0) }
                    )
                    Text(text = "PNG    JPG    JPEG    <10MB", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Garment Type", fontWeight = FontWeight.Bold, modifier = Modifier)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ToggleButton("Single Garment", selectedGarmentType) { uploadViewModel.setGarmentType(it) }
                        Spacer(modifier = Modifier.width(8.dp))
                        ToggleButton("Multiple Garments", selectedGarmentType) { uploadViewModel.setGarmentType(it) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    if (selectedGarmentType == "Multiple Garments") {
                        Row {
                            UploadBox(
                                imageUri = imageUris.getOrNull(1),
                                onUploadClick = { onUploadBoxClick(1) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            UploadBox(
                                imageUri = imageUris.getOrNull(2),
                                onUploadClick = { onUploadBoxClick(2) }
                            )
                        }
                    } else {
                        UploadBox(
                            imageUri = imageUris.getOrNull(1),
                            onUploadClick = { onUploadBoxClick(1) }
                        )
                    }
                }
            }
        }
        if (showError) {
            Alert(
                showDialog = showError,
                onDismiss = { showError = false },
                title = "Upload Required",
                message = "Please upload all required images to proceed. This step is mandatory and cannot be skipped.",
                confirmButtonText = "Confirm",
                onConfirmClick = { showError = false },
                onCancelClick = { showError = false },
                type = AlertType.ERROR
            )
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
                    val kebutuhan = if (selectedGarmentType == "Single Garment") 2 else 3
                    val terpenuhi = imageUris.take(kebutuhan).count { it != null }
                    if (terpenuhi < kebutuhan) {
                        showError = true
                        return@Button
                    }
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            val listImg = uploadViewModel.uploadImage(context, selectedGarmentType)
                            Log.d("ListImg", "Isi listImg = $listImg")
                            val newModel = ModelDto(
                                modelImage = listImg[0]!!,
                                userUid = user ?: ""
                            )
                            val responseModel = uploadViewModel.createModel(newModel)
                            Log.d("Model Result", "Response Model create: $responseModel")

                            val newGarment = GarmentDto(
                                garmentImage = listImg[1]!!,
                                userUid = user ?: ""
                            )
                            val responseGarment = uploadViewModel.createGarment(newGarment)
                            Log.d("Garment Result", "Response Garment create: $responseGarment")

                            val newTryon = if (selectedGarmentType == "Single Garment") {
                                SingleGarmentModel(
                                    userUid = user ?: "",
                                    modelImage = listImg[0]!!,
                                    garmentImage = listImg[1]!!
                                )
                            } else {
                                SingleGarmentModel(
                                    userUid = user ?: "",
                                    modelImage = listImg[0]!!,
                                    garmentImage = listImg[1]!!
                                )
                            }

                            val response = uploadViewModel.createRow(newTryon)
                            Log.d("VTO Result", "Response create: $response")

                            if (response.isSuccessful) {
                                val resultUrl = uploadViewModel.tryOnAfterUpload(listImg)
                                if (resultUrl != null) {
                                    Log.d("VTO Result", "Image URL: $resultUrl")
                                    val updateResult = SingleGarmentUpdateResult(
                                        resultImage = resultUrl
                                    )
                                    val updateResultImg = response.body()?.id?.let {
                                        uploadViewModel.updateResultImage(it, updateResult)
                                    }
                                    if (user != null) {
                                        loginViewModel.updateTotalGenerate(user)
                                    }
                                    isLoading = false
                                    navController.navigate("download?garmentType=Single Garment&id=${response.body()?.id}")
                                } else {
                                    isLoading = false
                                    Log.e("VTO", "Gagal mendapatkan hasil VTO")
                                    Toast.makeText(context, "Failed to get try-on result", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                isLoading = false
                                Log.e("TestLog", "Failed to create row: ${response.code()}")
                                Toast.makeText(context, "Upload failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Log.e("TestLog", "Upload or save failed: ${e.message}", e)
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Generate Try On", color = Color.White)
                }
            }
        }
    }
    if (isLoading) {
        Dialog(onDismissRequest = { /* gabisa di close */ }) {
            Card(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Processing...",
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Please wait while we generate your virtual try-on",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UploadBox(imageUri: Uri?, onUploadClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onUploadClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_input_add),
                contentDescription = "Upload",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun UploadOptionsDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .width(280.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Upload Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onGalleryClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("From Gallery")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onCameraClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Take Photo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onHistoryClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("History")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ToggleButton(label: String, selected: String, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(label) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected == label) Color(0xFFCDCDD0) else Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(text = label)
    }
}