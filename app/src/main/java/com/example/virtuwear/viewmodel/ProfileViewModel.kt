package com.example.virtuwear.viewmodel

import android.util.Log
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
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userResponse = MutableLiveData<Response<UserResponse>>()

    val userResponse: LiveData<Response<UserResponse>> get() = _userResponse

    val user = FirebaseAuth.getInstance().currentUser

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
                _userResponse.postValue(response)

                Log.d("ProfileViewModel", "Dashboard Response Success: ${response.body()}")
                Log.d("ProfileViewModel", "Dashboard Raw Response: $response")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Dashboard Error: ${e.message}", e)
            }
        }
    }


    fun getUserProfileById() {
        viewModelScope.launch {
            try {
                val userId = getUserId()
                val response = userRepository.getUserById(userId)
                _userResponse.postValue(response)

                Log.d("ProfileViewModel", "Profile Response Success: ${response.body()}")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Profile Error: ${e.message}", e)
            }
        }
    }

}
