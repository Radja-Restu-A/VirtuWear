package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.model.PurchaseDto
import com.example.virtuwear.data.service.CoinService
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val coinService: CoinService
) {
    suspend fun purchase(
        productId: String,
        purchaseToken: String,
        userUid: String
    ): Result<PurchaseDto> {
        return try {
            val purchase = PurchaseDto(
                purchaseToken = purchaseToken,
                productId = productId,
                userUid = userUid
            )
            val response = coinService.purchase(purchase)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("CoinRepo", "Purchase success: $body")
                    Result.success(body)
                } else {
                    Log.e("CoinRepo", "Purchase failed: empty body")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Log.e("CoinRepo", "Purchase failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("CoinRepo", "Purchase exception: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}
