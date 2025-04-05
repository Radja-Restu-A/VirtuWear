package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.service.AuthService
import com.example.virtuwear.data.model.UserRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    fun sendTokenToBackend(uid: String, email: String, name: String) {
        val userRequest = UserRequest(uid, email, name)

        authService.sendUserData(userRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Backend", "Data berhasil dikirim ke backend")
                } else {
                    Log.e("Backend", "Gagal mengirim data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Backend", "Error mengirim data", t)
            }
        })
    }
}
