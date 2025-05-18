package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.GarmentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GarmentService {
    @POST("api/garment")
    suspend fun createGarment(
        @Body model: GarmentDto
    ): Response<GarmentDto>

    @GET("api/garment/{userUid}")
    suspend fun getAllGarmentByUserId(
        @Path("userUid") id: String
    ) : Response<List<GarmentDto>>
}