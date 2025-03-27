    package com.example.virtuwear.screen

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.*

@Composable
fun UploadPhotoScreen(navController: NavController) {
    var selectedGarmentType by remember { mutableStateOf("Single Garment") }
    var imageUris by remember { mutableStateOf(listOf<Uri?>()) }
    val context = LocalContext.current

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
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Upload Photo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFEAECF0), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                UploadBox(imageUri = imageUris.getOrNull(0)) { uri ->
                    imageUris = listOf(uri)
                }

                Text(
                    text = "PNG    JPG    JPEG    <10MB",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Garment Type", fontWeight = FontWeight.Bold, modifier = Modifier)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ToggleButton("Single Garment", selectedGarmentType) { selectedGarmentType = it }
                    Spacer(modifier = Modifier.width(8.dp))
                    ToggleButton("Multiple Garments", selectedGarmentType) { selectedGarmentType = it }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedGarmentType == "Multiple Garments") {
                    Row {
                        UploadBox(imageUri = imageUris.getOrNull(2)) { uri ->
                            imageUris = listOf(uri, imageUris.getOrNull(1))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        UploadBox(imageUri = imageUris.getOrNull(3)) { uri ->
                            imageUris = listOf(imageUris.getOrNull(0), uri)
                        }
                    }
                } else if (selectedGarmentType == "Single Garment") {
                    UploadBox(imageUri = imageUris.getOrNull(1)) { uri ->
                        imageUris = listOf(uri)
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
                    val success = saveImageToStorage(context, imageUris)
                    if (success) {
                        navController.navigate("download")
                    } else {
                        Toast.makeText(context, "Gagal menyimpan gambar!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Generate Try On", color = Color.White)
            }
        }
    }
}

// Upload Box
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
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_input_add),
                contentDescription = "Upload",
                tint = Color.Gray
            )
        }
    }
}

// Toggle Button Type Garment
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

fun saveImageToStorage(context: Context, imageUris: List<Uri?>): Boolean {
    imageUris.forEachIndexed { index, uri ->
        if (uri != null) {
            val fileName = "${System.currentTimeMillis()}_${index + 1}.jpg"
            val folderName = if (index == 0) "model" else "outfit"

            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val outputStream: OutputStream?

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
                    }
                    val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    outputStream = imageUri?.let { context.contentResolver.openOutputStream(it) }
                } else {
                    val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName)
                    if (!storageDir.exists()) storageDir.mkdirs()
                    val file = File(storageDir, fileName)
                    outputStream = FileOutputStream(file)
                }

                inputStream?.copyTo(outputStream!!)
                inputStream?.close()
                outputStream?.close()
                Log.d("FileSaved", "Image saved at: $fileName")
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }
    return true
}
