package com.example.virtuwear.data.service

import com.example.virtuwear.data.model.UserRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface AuthService {
    @POST("api/auth/firebase")
    fun sendUserData(@Body userRequest: UserRequest): Call<Void>
}