
package com.example.virtuwear.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.model.KlingAiRequestDto
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import com.example.virtuwear.data.service.ImagebbApiService
import com.example.virtuwear.data.service.SingleGarmentService
import com.example.virtuwear.repository.KlingAiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val imageBBService: ImagebbApiService,
    private val singleGarmentService: SingleGarmentService,
    private val context: Application,
    private val tryOnHandler: KlingAiRepository
) : ViewModel() {
    var selectedGarmentType = mutableStateOf("Single Garment")
    var imageUris = mutableStateOf(listOf<Uri?>())
    val tryOnResultUrl = mutableStateOf<String?>(null)


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

    suspend fun uploadImage(): List<String?> {
        val apiKey = "98b550fd3318f73deb836066e8e2106b"
        val urlsView = mutableListOf<String?>()

        imageUris.value
            .filter { it != Uri.EMPTY }
            .forEachIndexed { i, uri ->
                val file = uri?.let { getRealFileFromUri(it) }
                val requestFile = file?.asRequestBody("image/*".toMediaTypeOrNull())
                val body = requestFile?.let {
                    MultipartBody.Part.createFormData("image", file.name, it)
                }

                if (body != null) {
                    try {
                        val response = imageBBService.uploadImage(apiKey, body)

                        if (response.isSuccessful) {
                            val urlView = response.body()?.data?.image?.url
                            val fixedViewerUrl = urlView?.replace("https://i.ibb.co", "https://i.ibb.co.com")
                            urlsView.add(fixedViewerUrl)
                            Log.d("UploadViewModel", "Image #$i uploaded: $fixedViewerUrl")
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                            Log.e("UploadViewModel", "Failed to upload image #$i: $errorMsg")
                        }
                    } catch (e: Exception) {
                        Log.e("UploadViewModel", "Upload gagal di image #$i: ${e.message}")
                    }
                }
            }

        Log.d("UploadViewModel", "Final URLs: $urlsView")
        return urlsView
    }


    private fun getRealFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_temp_", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createRow(singleGarmentModel: SingleGarmentModel): Response<SingleGarmentModel> {
        return try {
            val response = singleGarmentService.createGarment(singleGarmentModel)
            if (response.isSuccessful) {
                Log.d("SingleGarmentRepo", "Create garment success: ${response.body()}")
            } else {
                Log.e(
                    "SingleGarmentRepo",
                    "Create garment failed: Code=${response.code()}, Message=${response.message()}"
                )
            }
            response
        } catch (e: Exception) {
            Log.e("SingleGarmentRepo", "Exception during create garment: ${e.localizedMessage}", e)
            throw e
        }
    }

    fun updateResultImage(id: Long, model: SingleGarmentUpdateResult) {
        viewModelScope.launch {
            try {
                val response = singleGarmentService.updateResultImage(id, model)
                if (response.isSuccessful) {
                    Log.d("VTO", "Update berhasil: ${response.body()}")
                } else {
                    Log.e("VTO", "Update gagal: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("VTO", "Error update result image: ${e.message}")
            }
        }
    }


    suspend fun tryOnAfterUpload(urls: List<String?>): String? {
        val modelImg = urls.getOrNull(0)
        val clothImg = urls.getOrNull(1)

        if (modelImg != null && clothImg != null) {
            val request = KlingAiRequestDto(
                model_name = "kolors-virtual-try-on-v1-5",
                human_image = modelImg,
                cloth_image = clothImg,
                callback_url = "" // Kosong kalau tidak pakai callback, stelah hosting backend
            )

            val taskId = tryOnHandler.createTryOn(request)
            if (taskId != null) {
                val result = tryOnHandler.pollKlingApiUntilComplete(taskId)
                tryOnResultUrl.value = result
                return result
            }
            else {
                Log.e("VTO", "Failed to create task")
            }
        } else {
            Log.e("VTO", "Model or cloth image is null")
        }

        tryOnResultUrl.value = null
        return null
    }



}
