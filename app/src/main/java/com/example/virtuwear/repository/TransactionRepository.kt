package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.model.ModelDto
import com.example.virtuwear.data.model.TransactionDto
import com.example.virtuwear.data.service.TransactionService
import com.example.virtuwear.data.service.UserService
import retrofit2.Response
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionService: TransactionService
) {
//    suspend fun validateGenerate(userUid: String): Response<TransactionDto> {
//        return try {
//            val response = transactionService.validateGenerate(userUid)
//            if (response.isSuccessful) {
//                val body = response.body()
//                if (body != null) {
//                    Log.d("TransactionRepo", "Create Transaction success: $body")
//                    Result.success(body)
//                } else {
//                    Log.e("TransactionRepo", "Create Transaction failed: empty body")
//                    Result.failure(Exception("Empty response body"))
//                }
//            } else {
//                Log.e("TransactionRepo", "Create Transaction failed: ${response.code()} - ${response.message()}")
//                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
//            }
//        } catch (e: Exception) {
//            Log.e("ModeTransactionRepolRepo", "Create Transaction exception: ${e.localizedMessage}")
//            Result.failure(e)
//        }
//    }


}