package com.example.virtuwear.viewmodel

import android.app.Application
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
import com.example.virtuwear.data.ImagebbApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject



@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: ImagebbApiService,
    private val context: Application
) : ViewModel() {
    var selectedGarmentType = mutableStateOf("Single Garment")
    var imageUris = mutableStateOf(listOf<Uri?>())
    private var uploadedUrlsView = mutableStateOf(listOf<String?>())

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

    fun uploadImage() {
        viewModelScope.launch {
            val apiKey = "98b550fd3318f73deb836066e8e2106b"
            val urlsView = mutableListOf<String?>()

            imageUris.value
                .filter { it != Uri.EMPTY }
                .forEachIndexed { i, uri ->
                    val file = uri?.let { getRealPathFromUri(it) }?.let { File(it) }
                    val requestFile = file?.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = requestFile?.let {
                        MultipartBody.Part.createFormData("image", file.name, it)
                    }

                    if (body != null) {
                        try {
                            val response = repository.uploadImage(apiKey, body) // Pastikan tipe Response<ImgBBModel>

                            if (response.isSuccessful) {
                                val urlView = response.body()?.data?.urlViewer
                                val fixedViewerUrl = urlView?.replace("https://ibb.co", "https://ibb.co.com")
                                urlsView.add(fixedViewerUrl)
                            } else {
                                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                                Log.e("UploadViewModel", "Failed to upload image #$i: $errorMsg")
                            }
                        } catch (e: Exception) {
                            Log.e("UploadViewModel", "Upload gagal: ${e.message}")
                        }
                    }
                }
            uploadedUrlsView.value = urlsView
            Log.d("UploadViewModel", "ini link view: $uploadedUrlsView")
        }
    }


    private fun getRealPathFromUri(uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val path = cursor.getString(idx)
            cursor.close()
            path
        } else {
            uri.path ?: ""
        }
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
