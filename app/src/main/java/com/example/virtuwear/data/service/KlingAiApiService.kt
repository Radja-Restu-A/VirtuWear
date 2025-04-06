package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.KlingAiCreateResponseDto
import com.example.virtuwear.data.model.KlingAiRequestDto
import com.example.virtuwear.data.model.KlingAiSingleTaskResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface KlingAiApiService {
    @POST("klingai/tryon")
    suspend fun createTryOn(@Body request: KlingAiRequestDto): Response<KlingAiCreateResponseDto>

    @GET("klingai/tryon/{taskId}")
    suspend fun getTryOnTaskStatus(@Path("taskId") taskId: String): Response<KlingAiSingleTaskResponseDto>
}
