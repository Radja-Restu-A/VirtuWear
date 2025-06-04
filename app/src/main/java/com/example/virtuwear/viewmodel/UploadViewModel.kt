
package com.example.virtuwear.viewmodel

import android.R
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.State
import com.example.virtuwear.BuildConfig
import com.example.virtuwear.data.model.KlingAiRequestDto
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import com.example.virtuwear.data.service.ImagebbApiService
import com.example.virtuwear.data.service.SingleGarmentService
import com.example.virtuwear.repository.KlingAiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.max
import androidx.core.graphics.createBitmap
import com.example.virtuwear.data.model.GarmentDto
import com.example.virtuwear.data.model.ModelDto
import com.example.virtuwear.data.model.TransactionDto
import com.example.virtuwear.data.service.TransactionService
import com.example.virtuwear.repository.GarmentRepository
import com.example.virtuwear.repository.ModelRepository
import com.example.virtuwear.repository.TransactionRepository
import retrofit2.Response
import java.net.URL
import java.io.InputStream

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val imageBBService: ImagebbApiService,
    private val singleGarmentService: SingleGarmentService,
    private val context: Application,
    private val tryOnHandler: KlingAiRepository,
    private val modelRepository: ModelRepository,
    private val garmentRepository: GarmentRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionService: TransactionService
) : ViewModel() {
    var selectedGarmentType = mutableStateOf("Single Garment")
    var imageUris = mutableStateOf(listOf<Uri?>())
    var imageUriSources = mutableStateOf(listOf<String?>()) // "Local" atau "History"
    val tryOnResultUrl = mutableStateOf<String?>(null)

    private val _modelList = mutableStateOf<List<ModelDto>>(emptyList())
    val modelList: State<List<ModelDto>> = _modelList

    private val _garmentList = mutableStateOf<List<GarmentDto>>(emptyList())
    val garmentList: State<List<GarmentDto>> = _garmentList

    fun setGarmentType(type: String) {
        selectedGarmentType.value = type
    }

    fun addImageUris(uri: Uri, index: Int, source: String = "Local") {
        val newList = imageUris.value.toMutableList()
        val newSourceList = imageUriSources.value.toMutableList()

        // Buat bikin list dulu nantinya ditampung
        while (newList.size <= index) {
            newList.add(null)
        }
        while (newSourceList.size <= index) {
            newSourceList.add(null)
        }

        newList[index] = uri
        newSourceList[index] = source

        imageUris.value = newList
    }

    suspend fun uploadImage(context: Context, garmentType: String): List<String?> {
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.IMAGE_BB_API_KEY
            val finalUrls = mutableListOf<String?>()

            try {
                if (garmentType == "Single Garment") {
                    val modelUrl = processImage(context, 0, apiKey)
                    finalUrls.add(modelUrl)

                    val garmentUrl = processImage(context, 1, apiKey)
                    finalUrls.add(garmentUrl)

                } else { // Multiple Garments
                    val modelUrl = processImage(context, 0, apiKey)
                    finalUrls.add(modelUrl)

                    val combinedUrl = processCombinedGarments(context, apiKey)
                    finalUrls.add(combinedUrl)
                }

            } catch (e: Exception) {
                Log.e("UploadViewModel", "Error in uploadImage: ${e.message}")
            }

            Log.d("UploadViewModel", "Final URLs: $finalUrls")
            finalUrls
        }
    }

    private suspend fun processImage(context: Context, index: Int, apiKey: String): String? {
        val uri = imageUris.value.getOrNull(index) ?: return null
        val source = imageUriSources.value.getOrNull(index) ?: "Local"

        return if (source == "History") {
            // Jika dari history, uri sudah berupa URL imgBB, langsung return
            uri.toString()
        } else {
            // Jika dari local, upload ke imgBB
            uploadSingleImageToImgBB(context, uri, apiKey)
        }
    }

    private suspend fun processCombinedGarments(context: Context, apiKey: String): String? {
        val uri1 = imageUris.value.getOrNull(1) ?: return null
        val uri2 = imageUris.value.getOrNull(2) ?: return null
        val source1 = imageUriSources.value.getOrNull(1) ?: "Local"
        val source2 = imageUriSources.value.getOrNull(2) ?: "Local"

        return withContext(Dispatchers.IO) {
            try {
                // Download images jika dari history, atau gunakan langsung jika local
                val bitmap1 = getBitmapFromUriOrUrl(context, uri1, source1)
                val bitmap2 = getBitmapFromUriOrUrl(context, uri2, source2)

                if (bitmap1 == null || bitmap2 == null) {
                    Log.e("UploadViewModel", "Failed to get bitmaps for combining")
                    return@withContext null
                }

                // Combine images
                val combinedBitmap = createBitmap(
                    max(bitmap1.width, bitmap2.width),
                    bitmap1.height + bitmap2.height
                )

                val canvas = Canvas(combinedBitmap)
                canvas.drawBitmap(bitmap1, 0f, 0f, null)
                canvas.drawBitmap(bitmap2, 0f, bitmap1.height.toFloat(), null)

                // Simpen combine ke temp file
                val tempFile = File.createTempFile(
                    "combined_${System.currentTimeMillis()}",
                    ".jpg",
                    context.cacheDir
                )

                FileOutputStream(tempFile).use {
                    combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
                }

                // Upload to imgBB
                val combinedUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    tempFile
                )

                val result = uploadSingleImageToImgBB(context, combinedUri, apiKey)

                // Cleanup
                bitmap1.recycle()
                bitmap2.recycle()
                combinedBitmap.recycle()
                tempFile.delete()

                result

            } catch (e: Exception) {
                Log.e("UploadViewModel", "Error combining images: ${e.message}")
                null
            }
        }
    }

    private suspend fun getBitmapFromUriOrUrl(context: Context, uri: Uri, source: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                if (source == "History") {
                    // Download dari URL
                    val url = URL(uri.toString())
                    val inputStream: InputStream = url.openConnection().getInputStream()
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    // Load dari local URI
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Error getting bitmap from ${source}: ${e.message}")
                null
            }
        }
    }

    private suspend fun uploadSingleImageToImgBB(context: Context, uri: Uri, apiKey: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val file = getRealFileFromUri(uri) ?: return@withContext null
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = imageBBService.uploadImage(apiKey, body)

                if (response.isSuccessful) {
                    val urlView = response.body()?.data?.image?.url
                    val fixedViewerUrl = urlView?.replace("https://i.ibb.co", "https://i.ibb.co.com")
                    Log.d("UploadViewModel", "Image uploaded: $fixedViewerUrl")
                    fixedViewerUrl
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("UploadViewModel", "Failed to upload image: $errorMsg")
                    null
                }
            } catch (e: Exception) {
                Log.e("UploadViewModel", "Upload failed: ${e.message}")
                null
            }
        }
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

    suspend fun createModel(modelDto: ModelDto): Result<ModelDto> {
        return modelRepository.create(modelDto)
    }

    suspend fun validateGenerate(userUid: String): Response<Map<String, String>> {
        return transactionService.validateGenerate(userUid)
    }

    suspend fun reduceCoin(userUid: String): Response<TransactionDto> {
        return transactionService.reduceCoin(userUid)
    }


    suspend fun createGarment(garmentDto: GarmentDto): Result<GarmentDto> {
        return garmentRepository.create(garmentDto)
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
                callback_url = "" //  hosting backend dulu
            )

            val taskId = tryOnHandler.createTryOn(request)
            if (taskId != null) {
                val result = tryOnHandler.pollKlingApiUntilComplete(taskId)
                tryOnResultUrl.value = result
                return result
            } else {
                Log.e("VTO", "Failed to create task")
            }
        } else {
            Log.e("VTO", "Model or cloth image is null")
        }

        tryOnResultUrl.value = null
        return null
    }

    private suspend fun combineTwoImages(context: Context, uri1: Uri, uri2: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap1 = context.contentResolver.openInputStream(uri1)?.use {
                    BitmapFactory.decodeStream(it)
                }
                val bitmap2 = context.contentResolver.openInputStream(uri2)?.use {
                    BitmapFactory.decodeStream(it)
                }

                if (bitmap1 == null || bitmap2 == null) return@withContext null

                val combinedBitmap = createBitmap(max(bitmap1.width, bitmap2.width), bitmap1.height + bitmap2.height)

                val canvas = Canvas(combinedBitmap)
                canvas.drawBitmap(bitmap1, 0f, 0f, null)
                canvas.drawBitmap(bitmap2, 0f, bitmap1.height.toFloat(), null)

                val tempFile = File.createTempFile(
                    "combined_${System.currentTimeMillis()}",
                    ".jpg",
                    context.cacheDir
                )

                FileOutputStream(tempFile).use {
                    combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
                }

                bitmap1.recycle()
                bitmap2.recycle()

                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    tempFile
                )
            } catch (e: Exception) {
                Log.e("CombineImages", "Error combining images: ${e.message}")
                null
            }
        }
    }

    fun getModel(id: String) {
        viewModelScope.launch {
            try {
                val response = modelRepository.getAllModelById(id)
                if (response.isSuccessful) {
                    val models = response.body() ?: emptyList()
                    _modelList.value = models
                    Log.d("VTO", "Get model bisa buat: $id, count: ${models.size}")
                } else {
                    _modelList.value = emptyList()
                    Log.e("VTO", "Gagal ambil model by user id: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _modelList.value = emptyList()
                Log.e("VTO", "Gagal ambil model by user id: ${e.localizedMessage}")
            }
        }
    }

    fun getGarment(id: String) {
        viewModelScope.launch {
            try {
                val response = garmentRepository.GetAllGarmentByUserId(id)
                if (response.isSuccess) {
                    val garments = response.getOrNull() ?: emptyList()
                    _garmentList.value = garments
                    Log.d("VTO", "Get garment bisa buat: $id, count: ${garments.size}")
                } else {
                    _garmentList.value = emptyList()
                    Log.e("VTO", "Gagal ambil garment by user id: $id")
                }
            } catch (e: Exception) {
                _garmentList.value = emptyList()
                Log.e("VTO", "Gagal ambil garment by user id: ${e.message}")
            }
        }
    }
}
