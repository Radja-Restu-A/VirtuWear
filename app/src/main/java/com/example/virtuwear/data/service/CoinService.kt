package com.example.virtuwear.data.service
import com.example.virtuwear.data.model.DoubleGarmentResponse
import com.example.virtuwear.data.model.PurchaseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface CoinService {

    @POST("api/coin/purchase")
    suspend fun purchase(
        @Body model: PurchaseDto
    ): Response<PurchaseDto>

}