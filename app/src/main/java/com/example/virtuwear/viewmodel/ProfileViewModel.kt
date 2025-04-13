package com.example.virtuwear.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.dao.ProfileDao
import com.example.virtuwear.data.entity.ProfileEntity
import com.example.virtuwear.data.model.UserRequest
import com.example.virtuwear.data.model.UserResponse
import com.example.virtuwear.data.service.UserService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (
    private val userService: UserService
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

    fun getUserProfileById() {
        viewModelScope.launch {
            try {
                val userId = getUserId()
                val response = userService.getUserById(userId)
                _userResponse.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
//    fun insertProfile(profile: ProfileEntity) {
//        viewModelScope.launch {
//            profileDao.insert(profile)
//        }
//    }
//
//    fun getProfileByEmail(email: String, callback: (ProfileEntity?) -> Unit) {
//        viewModelScope.launch {
//            val profile = profileDao.getProfileByEmail(email)
//            callback(profile)
//        }
//    }
//
//    fun updateKoinByEmail(email: String, koin: Int) {
//        viewModelScope.launch {
//            profileDao.updateKoinByEmail(email, koin)
//        }
//    }
}
