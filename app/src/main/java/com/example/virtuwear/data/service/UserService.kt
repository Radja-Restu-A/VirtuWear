package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.ProfileResponse
import com.example.virtuwear.data.model.UserResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): Response<UserResponse>

    @GET("api/users/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): Response<ProfileResponse>


    @POST("api/users/update/dashboard/{uid}")
    suspend fun updateDashboard(@Path("uid") userId: String): Response<UserResponse>

    @POST("api/users/{uid}/total_generate")
    suspend fun updateTotalGenerate(@Path("uid") userId: String): Response<UserResponse>

    @POST("api/users/{uid}/redeem-referral/{referralCode}")
    suspend fun redeemReferral(
        @Path("uid") userId: String,
        @Path("referralCode") redeemedReferral: String
    ): Response<UserResponse>

    @DELETE("api/users/{uid}")
    suspend fun deleteUser(@Path("uid") userId: String): Response<ResponseBody>
}