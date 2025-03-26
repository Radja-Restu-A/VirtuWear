package com.example.virtuwear.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.virtuwear.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    fun getGoogleSignInClient(): GoogleSignInClient {
        val context = getApplication<Application>().applicationContext
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun getUserData(): Map<String, String?> {
        val user = getCurrentUser()
        return mapOf(
            "uid" to user?.uid,
            "email" to user?.email,
            "displayName" to user?.displayName
        )
    }

}