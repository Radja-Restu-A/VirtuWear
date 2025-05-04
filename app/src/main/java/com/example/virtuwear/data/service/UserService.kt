package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.UserRequest
import com.example.virtuwear.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): Response<UserResponse>

    @PUT("api/users/update/dashboard/{uid}")
    suspend fun updateDashboard(@Path("userId") userId: String): Response<UserResponse>
}