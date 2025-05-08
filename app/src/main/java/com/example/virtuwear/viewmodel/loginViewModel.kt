package com.example.virtuwear.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.R
import com.example.virtuwear.data.AppDatabase
import com.example.virtuwear.data.entity.ProfileEntity
import com.example.virtuwear.repository.AuthRepository
import com.example.virtuwear.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : AndroidViewModel(application) {
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

    fun insertProfile(email: String, koin: Int) {
        viewModelScope.launch {
            val existingProfile = daoProfile.getProfileByEmail(email)
            if (existingProfile == null) {
                daoProfile.insert(
                    ProfileEntity(
                    email = email,
                    koin = koin
                    )
                )
            } else {
                daoProfile.updateKoinByEmail(email, koin)
            }
        }
    }

    fun sendTokenToBackend(uid: String, email: String, name: String) {
        authRepository.sendTokenToBackend(uid, email, name)
    }

    suspend fun updateTotalGenerate(userId: String) {
        userRepository.updateTotalGenerate(userId)
    }
}