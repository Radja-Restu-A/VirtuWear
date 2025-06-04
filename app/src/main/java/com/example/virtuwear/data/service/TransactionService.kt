package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.TransactionDto
import retrofit2.Response

import retrofit2.http.POST
import retrofit2.http.GET

import retrofit2.http.Path

interface TransactionService {

    @GET("api/transaction/validate/{userUid}")
    suspend fun validateGenerate(@Path("userUid") userUid: String): Response<Map<String, String>>


    @POST("api/transaction/reduce/{userUid}")
    suspend fun reduceCoin(@Path("userUid") userUid: String): Response<TransactionDto>
}