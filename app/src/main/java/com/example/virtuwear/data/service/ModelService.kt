package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.ModelDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ModelService {
    @POST("api/model")
    suspend fun createModel(
        @Body model: ModelDto
    ): Response<ModelDto>

    @GET("api/model/{userUid}")
    suspend fun getAllModelByUserId(
        @Path("userUid") id: String
    ) : Response<List<ModelDto>>
}