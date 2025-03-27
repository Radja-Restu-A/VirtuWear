package com.example.virtuwear.viewmodel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class UploadViewModel : ViewModel() {
    var selectedGarmentType = mutableStateOf("Single Garment")
    var imageUris = mutableStateOf(listOf<Uri?>())

    fun setGarmentType(type: String) {
        selectedGarmentType.value = type
    }

    fun addImageUris(uri: Uri, index: Int) {
        val newList = imageUris.value.toMutableList()
        if (index < newList.size) {
            newList[index] = uri
        } else {
            newList.add(uri)
        }
        imageUris.value = newList
    }

    fun saveImages(context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val success = imageUris.value.mapIndexed { index, uri -> saveImage(context, uri, index) }.all { it }
            if (success) onSuccess() else onError()
        }
    }

    private fun saveImage(context: Context, uri: Uri?, index: Int): Boolean {
        if (uri == null) return false
        val fileName = "${System.currentTimeMillis()}.jpg"
        val baseFolderName = "virtuwear"
        val subFolderName = if (index == 0) "model" else "outfit"

        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream: OutputStream?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$baseFolderName/$subFolderName")
                }
                val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                outputStream = imageUri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                val storageDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "$baseFolderName/$subFolderName"
                )
                if (!storageDir.exists()) storageDir.mkdirs()
                val file = File(storageDir, fileName)
                outputStream = FileOutputStream(file)
            }

            inputStream?.copyTo(outputStream!!)
            inputStream?.close()
            outputStream?.close()
            Log.d("FileSaved", "Image saved at: $baseFolderName/$subFolderName/$fileName")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}