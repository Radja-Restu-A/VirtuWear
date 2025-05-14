package com.example.virtuwear.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.dao.ProfileDao
import com.example.virtuwear.data.entity.ProfileEntity
import com.example.virtuwear.data.model.UserRequest
import com.example.virtuwear.data.model.UserResponse
import com.example.virtuwear.data.service.UserService
import com.example.virtuwear.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userResponse = MutableStateFlow<UserResponse?>(null)
    val userResponse: StateFlow<UserResponse?> = _userResponse.asStateFlow()

    val user = FirebaseAuth.getInstance().currentUser

    private val _redeemCodeStatus = MutableStateFlow<Result<Unit>?>(null)
    val redeemCodeStatus: StateFlow<Result<Unit>?> = _redeemCodeStatus

    fun fetchUser() {
        viewModelScope.launch {
            try {
                val response = userRepository.getUserById(getUserId())
                if (response.isSuccessful) {
                    _userResponse.value = response.body()
                } else {
                    _userResponse.value = null
                    Log.e("fetchUser", "API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _userResponse.value = null
                Log.e("fetchUser", "Exception: ${e.message}")
            }
        }
    }



    fun getUserName(): String? {
        return user?.displayName
    }

    fun getUserId(): String {
        return user?.uid ?: throw IllegalStateException("User not logged in")
    }

    fun getDashboardById() {
        viewModelScope.launch {
            try {
                val userId = getUserId()
                val response = userRepository.updateDashboard(userId)
                _userResponse.value = response.body()

                Log.d("ProfileViewModel", "Dashboard Response Success: ${response.body()}")
                Log.d("ProfileViewModel", "Dashboard Raw Response: $response")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Dashboard Error: ${e.message}", e)
            }
        }
    }

    fun redeemReferralCode(code: String) {
        viewModelScope.launch {
            try {
                userRepository.redeemReferralCode(getUserId(), code)
                _redeemCodeStatus.value = Result.success(Unit)
                Log.e("ProfileScreen", "berhasil reedem")

            } catch (e: Exception) {
                Log.e("ProfileScreen", "gagal reedem", e)
                _redeemCodeStatus.value = Result.failure(e)
            }
        }
    }
    fun clearRedeemStatus() {
        _redeemCodeStatus.value = null
    }



    fun getUserProfileById() {
        viewModelScope.launch {
            try {
                val userId = getUserId()
                val response = userRepository.getUserById(userId)
                if (response.isSuccessful) {
                    _userResponse.value
                    (response.body())
                } else {
                    _userResponse.value = null
                    Log.e("fetchUser", "Error: ${response.errorBody()?.string()}")
                }
                Log.d("ProfileViewModel", "Profile Response Success: ${response.body()}")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Profile Error: ${e.message}", e)
            }
        }
    }

}
