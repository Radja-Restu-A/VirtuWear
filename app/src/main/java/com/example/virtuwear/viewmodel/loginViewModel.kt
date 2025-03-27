package com.example.virtuwear.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.R
import com.example.virtuwear.data.AppDatabase
import com.example.virtuwear.data.ProfileEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback
import org.json.JSONObject
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val daoProfile = AppDatabase.getDatabase(application).profileDao

    fun getGoogleSignInClient(): GoogleSignInClient {
        val context = getApplication<Application>().applicationContext
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun insertProfile(email:String, koin:Int){
        viewModelScope.launch {
            daoProfile.insert(ProfileEntity(
                email = email,
                koin = koin
            ))
        }
    }

    fun sendTokenToBackend(uid: String, email: String, name: String,) {
        val referralObject = JSONObject().apply {
            put("referralCode", "-")
            put("totalUsed", 0L)
            put("cooldown", "2024-03-30T12:00:00") // format ISO biar aman
        }

        val jsonObject = JSONObject().apply {
            put("uid", uid)
            put("email", email)
            put("name", name)
            put("token", 0)
            put("totalTryon", 0)
            put("totalGenerate", 0)
            put("redeemedReferral", "-")
            put("referral", referralObject)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://172.20.10.2:8080/api/auth/firebase") // Ganti jika pakai IP lokal
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Backend", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    Log.d("Backend", "Response: $responseBody")
                }
            }
        })
    }
}