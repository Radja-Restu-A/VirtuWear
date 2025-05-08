package com.example.virtuwear.screen

import android.R
import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.virtuwear.components.Alert
import com.example.virtuwear.components.AlertType
import com.example.virtuwear.components.font
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import com.example.virtuwear.repository.UserRepository
import com.example.virtuwear.viewmodel.UploadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.virtuwear.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun UploadPhotoScreen(
    navController: NavController,
    uploadViewModel: UploadViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showError by remember { mutableStateOf(false) }
    val selectedGarmentType by uploadViewModel.selectedGarmentType
    val imageUris by uploadViewModel.imageUris
    var isLoading by remember { mutableStateOf(false) }


    val firebase = loginViewModel.getCurrentUser()
    val user = firebase?.uid


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
            TextButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    loginViewModel.getGoogleSignInClient().signOut().addOnCompleteListener {
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Logout", color = Color.Red)
            }
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
                    UploadBox(imageUri = imageUris.getOrNull(0)) { uri -> uploadViewModel.addImageUris(uri, 0) }
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
                            UploadBox(imageUri = imageUris.getOrNull(1)) { uri -> uploadViewModel.addImageUris(uri, 1) }
                            Spacer(modifier = Modifier.width(8.dp))
                            UploadBox(imageUri = imageUris.getOrNull(2)) { uri -> uploadViewModel.addImageUris(uri, 2) }
                        }
                    } else {
                        UploadBox(imageUri = imageUris.getOrNull(1)) { uri -> uploadViewModel.addImageUris(uri, 1) }
                    }
                }
            }
        }
        if (showError) {
            Alert(
                showDialog = showError,
                onDismiss = { showError = false },
                title = "Anda belum upload foto untuk salah satu",
                message = "Pastikan gambar sudah di upload keseluruhan agar kami bisa memproses!",
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
                            val listImg = uploadViewModel.uploadImage()

                            val newModel = SingleGarmentModel(
                                userId = user ?: "",
                                modelImg = listImg.component1(),
                                garmentImg = listImg.component2()
                            )

                            val response = uploadViewModel.createRow(newModel)
                            Log.d("VTO Result", "Response create: $response")

                            if (response.isSuccessful) {
                                val resultUrl = uploadViewModel.tryOnAfterUpload(listImg)
                                if (resultUrl != null) {
                                    Log.d("VTO Result", "Image URL: $resultUrl")
                                    val updateResult = SingleGarmentUpdateResult(
                                        resultImg = resultUrl
                                    )
                                    val updateResultImg = response.body()?.idSingle?.let {
                                        uploadViewModel.updateResultImage(
                                            it, updateResult)
                                    }
                                    if (user != null) {
                                        loginViewModel.updateTotalGenerate(user)
                                    }

                                    isLoading = false
                                    navController.navigate("download?garmentType=Single Garment&id=${response.body()?.idSingle}")
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
fun UploadBox(imageUri: Uri?, onImageSelected: (Uri) -> Unit) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { imagePickerLauncher.launch("image/*") },
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