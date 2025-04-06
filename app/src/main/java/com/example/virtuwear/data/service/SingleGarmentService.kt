package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SingleGarmentService {
    @GET("api/single-garments/{userId}")
    suspend fun getAllGarmentsByUser(
        @Path("userId") userId: String
    ): Response<List<SingleGarmentModel>>

    @GET("api/single-garments/detail/{id}")
    suspend fun getSingleGarmentById(
        @Path("id") id: Long
    ): Response<SingleGarmentModel>

    @POST("api/single-garments")
    suspend fun createGarment(
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/single-garments/{id}")
    suspend fun updateGarment(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @DELETE("api/single-garments/{id}")
    suspend fun deleteGarment(
        @Path("id") id: Long
    ): Response<Unit>

    @PUT("api/single-garments/update/model/{id}")
    suspend fun updateModelImage(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/single-garments/update/garment/{id}")
    suspend fun updateGarmentImage(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/single-garments/update/result/{id}")
    suspend fun updateResultImage(
        @Path("id") id: Long,
        @Body model: SingleGarmentUpdateResult
    ): Response<SingleGarmentModel>

    @PUT("api/single-garments/update/outfit_name/{id}")
    suspend fun updateOutfitName(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/single-garments/update/notes/{id}")
    suspend fun updateNotes(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/single-garments/update/bookmark/{id}")
    suspend fun updateBookmark(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>
}