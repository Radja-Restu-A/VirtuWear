package com.example.virtuwear.repository

import com.example.virtuwear.data.ImagebbApiService
import okhttp3.MultipartBody
import javax.inject.Inject

class ImageBBRepository @Inject constructor(private val api: ImagebbApiService) {

    suspend fun uploadImage(apiKey: String, image: MultipartBody.Part): Result<String> {
        return try {
            val response = api.uploadImage(apiKey, image)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.url)
            } else {
                Result.failure(Exception("Upload gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}