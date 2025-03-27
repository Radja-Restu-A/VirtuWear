package com.example.virtuwear.screen

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


@Composable
fun DownloadPhotoScreen(navController: NavController) {
    val context = LocalContext.current
    val modelPhoto = getLatestPhoto(context, "model")
    val outfitPhoto = getLatestPhoto(context, "outfit")

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
                text = "Download Photo",
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
                        outfitPhoto?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "Outfit Photo",
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Outfit Photo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    modelPhoto?.let { downloadPhoto(context, it) }
                    outfitPhoto?.let { downloadPhoto(context, it) }
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


fun getLatestPhoto(context: Context, folderName: String): Uri? {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
    )
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf("%Pictures/$folderName%")

    context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = cursor.getLong(idColumn)
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
        }
    }
    return null
}

fun downloadPhoto(context: Context, uri: Uri) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val fileName = "Download_${System.currentTimeMillis()}.jpg"
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    Toast.makeText(context, "Photo downloaded to Downloads folder", Toast.LENGTH_SHORT).show()
}
