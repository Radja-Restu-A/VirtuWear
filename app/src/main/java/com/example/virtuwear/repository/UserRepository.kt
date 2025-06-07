package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.model.UserResponse
import com.example.virtuwear.data.service.UserService
import retrofit2.Response
import retrofit2.http.Path
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {
    suspend fun getUserById(userId: String) = userService.getUserById(userId)
    suspend fun updateDashboard(userId: String): Response<UserResponse> {
        val response = userService.updateDashboard(userId)
        Log.d("UserRepository", "API Response: ${response.body()}")
        return response
    }
    suspend fun updateTotalGenerate(userId: String) = userService.updateTotalGenerate(userId)

    suspend fun getProfile(userId: String) = userService.getProfile(userId)


    suspend fun redeemReferralCode(userId: String, redeemedReferral: String) = userService.redeemReferral(userId, redeemedReferral)

    suspend fun deleteUser(userId: String):String{
        return try{
            val response = userService.deleteUser(userId)
            if (response.isSuccessful){
                Log.d("UserRepository", "success to delete user")
                "User succesfully deleted"
            }else{
                Log.e("UserRepository", "failed to delete user")
                "User failed to delete"
            }
        }catch (e:Exception){
            Log.e("UserRepository", "error to delete user",e)
            "Error occurred while deleting user: ${e.message}"
        }
    }
}
