package com.example.virtuwear.data.service
import com.example.virtuwear.data.model.DoubleGarmentModel
import com.example.virtuwear.data.model.DoubleGarmentResponse
import com.example.virtuwear.data.model.DoubleGarmentUpdateBookmark
import com.example.virtuwear.data.model.SingleGarmentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface DoubleGarmentService {

    @PUT("api/double-garments/update/bookmark-double/{id}")
    suspend fun updateBookmark(
        @Path("id") id: Long,
        @Body model: DoubleGarmentUpdateBookmark
    ): Response<DoubleGarmentUpdateBookmark>

    @GET("api/double-garments/bookmarked")
    suspend fun getBookmarkedItems(): Response<List<DoubleGarmentResponse>>

}