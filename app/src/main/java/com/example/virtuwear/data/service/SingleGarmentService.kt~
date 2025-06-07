package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.SingleGarmentDto
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentResponse
import com.example.virtuwear.data.model.SingleGarmentUpdateBookmark
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SingleGarmentService {
    @GET("api/tryon/{userUid}")
    suspend fun getAllGarmentsByUser(
        @Path("userUid") userUid: String
    ): Response<List<SingleGarmentModel>>

    @GET("api/tryon/search")
    suspend fun searchByOutfitName(
        @Query("outfitName") outfitName: String
    ): Response<List<SingleGarmentModel>>

    @GET("api/tryon/detail/{id}")
    suspend fun getSingleGarmentById(
        @Path("id") id: Long
    ): Response<SingleGarmentResponse>

    @POST("api/tryon")
    suspend fun createGarment(
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/tryon/{id}")
    suspend fun updateGarment(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @DELETE("api/tryon/{id}")
    suspend fun deleteGarment(
        @Path("id") id: Long
    ): Response<Unit>

    @PUT("api/tryon/update/garment/{id}")
    suspend fun updateGarmentImage(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/tryon/update/model/{id}")
    suspend fun updateModelImage(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/tryon/update/result/{id}")
    suspend fun updateResultImage(
        @Path("id") id: Long,
        @Body model: SingleGarmentUpdateResult
    ): Response<SingleGarmentModel>

    @PUT("api/tryon/update/outfit_name/{id}")
    suspend fun updateOutfitName(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/tryon/update/notes/{id}")
    suspend fun updateNotes(
        @Path("id") id: Long,
        @Body model: SingleGarmentModel
    ): Response<SingleGarmentModel>

    @PUT("api/tryon/update/bookmark/{id}")
    suspend fun updateBookmark(
        @Path("id") id: Long,
        @Body dto: SingleGarmentDto
    ): Response<SingleGarmentDto>


    @GET("api/tryon/bookmarked/{userUid}")
    suspend fun getBookmarkedItems(
        @Path("userUid") userUid: String
    ): Response<List<SingleGarmentModel>>

    @GET("api/tryon/sort/date/{timeMillis}")
    suspend fun findByCreatedAt(
        @Path("timeMillis") timeMillis: Long
    ): Response<List<SingleGarmentModel>>
}