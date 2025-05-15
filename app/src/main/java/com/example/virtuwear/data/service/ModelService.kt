package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.ModelDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ModelService {
    @POST("api/model")
    suspend fun createModel(
        @Body model: ModelDto
    ): Response<ModelDto>
}