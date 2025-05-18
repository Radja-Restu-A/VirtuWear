package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.GarmentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GarmentService {
    @POST("api/garment")
    suspend fun createGarment(
        @Body model: GarmentDto
    ): Response<GarmentDto>
}