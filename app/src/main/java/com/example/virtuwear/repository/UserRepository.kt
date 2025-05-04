package com.example.virtuwear.repository

import com.example.virtuwear.data.model.UserResponse
import com.example.virtuwear.data.service.UserService
import retrofit2.Response
import retrofit2.http.Path
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {
    suspend fun getUserById(userId: String) = userService.getUserById(userId)
    suspend fun updateDashboard(userId: String) = userService.updateDashboard(userId)

}
