package com.example.virtuwear.data

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.Response

interface ImagebbApiService {
    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): Response<ImgBBModel> // HARUS menggunakan Response<>
}
