package com.example.virtuwear.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import com.example.virtuwear.data.service.SingleGarmentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val singleGarmentService: SingleGarmentService
) : ViewModel() {

    private val _modelPhoto = MutableStateFlow<Uri?>(null)
    val modelPhoto: StateFlow<Uri?> = _modelPhoto

    private val _outfitPhotos = MutableStateFlow<List<Uri>>(emptyList())
    val outfitPhotos: StateFlow<List<Uri>> = _outfitPhotos

    private val _isDetailsVisible = MutableStateFlow(false)
    val isDetailsVisible: StateFlow<Boolean> = _isDetailsVisible

    fun toggleDetailsVisibility() {
        _isDetailsVisible.value = !_isDetailsVisible.value
    }

    fun getLatestPhoto(context: Context, garmentType: String) {
        viewModelScope.launch {
            getLatestFromFolder(context, "virtuwear/model", "model")
            getLatestOutfitsFromFolder(context, "virtuwear/outfit", garmentType)
        }
    }

    private fun getLatestFromFolder(context: Context, folderPath: String, type: String) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Pictures/$folderPath%")

        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(idColumn)
                    val photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                    if (type == "model") _modelPhoto.value = photoUri
                } else {
                    if (type == "model") _modelPhoto.value = null
                }
            } ?: run {
                if (type == "model") _modelPhoto.value = null
            }
        } catch (e: Exception) {
            if (type == "model") _modelPhoto.value = null
            Toast.makeText(context, "Error accessing $folderPath: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLatestOutfitsFromFolder(context: Context, folderPath: String, garmentType: String) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Pictures/$folderPath%")
        val outfitList = mutableListOf<Uri>()
        val maxPhotos = if (garmentType == "Single Garment") 1 else 2

        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                var count = 0
                while (cursor.moveToNext() && count < maxPhotos) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(idColumn)
                    val photoUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                    outfitList.add(photoUri)
                    count++
                }
                _outfitPhotos.value = outfitList
            } ?: run {
                _outfitPhotos.value = emptyList()
            }
        } catch (e: Exception) {
            _outfitPhotos.value = emptyList()
            Toast.makeText(context, "Error accessing $folderPath: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadPhoto(context: Context, imageUrl: String, fileNameInput: String, garmentId: Long) {
        viewModelScope.launch {
            val fileName = if (fileNameInput.endsWith(".jpg", true)) fileNameInput else "$fileNameInput.jpg"
            try {
                withContext(Dispatchers.IO) {
                    val response = singleGarmentService
                    val url = URL(imageUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()

                    val inputStream = connection.inputStream
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        fileName
                    )
                    val outputStream = FileOutputStream(file)

                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                }

                val updateResult = SingleGarmentUpdateResult(resultImg = fileName)
                val response =  singleGarmentService.updateResultImage(garmentId, updateResult)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Photo berhasil diganti namanya", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "gagal memperbarui result img: ${response.message()}", Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(context, "Gambar berhasil diunduh ke folder Downloads!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal download gambar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}