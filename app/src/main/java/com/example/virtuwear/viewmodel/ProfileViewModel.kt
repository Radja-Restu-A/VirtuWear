package com.example.virtuwear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.dao.ProfileDao
import com.example.virtuwear.data.entity.ProfileEntity
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileDao: ProfileDao) : ViewModel() {

    fun insertProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            profileDao.insert(profile)
        }
    }

    fun getProfileByEmail(email: String, callback: (ProfileEntity?) -> Unit) {
        viewModelScope.launch {
            val profile = profileDao.getProfileByEmail(email)
            callback(profile)
        }
    }

    fun updateKoinByEmail(email: String, koin: Int) {
        viewModelScope.launch {
            profileDao.updateKoinByEmail(email, koin)
        }
    }
}
