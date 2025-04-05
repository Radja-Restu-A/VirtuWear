package com.example.virtuwear.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.dao.ImageDao
import com.example.virtuwear.data.entity.ImageEntity
import kotlinx.coroutines.launch

class ImageViewModel(private val imageDao: ImageDao) : ViewModel() {

    fun insertImage(imageEntity: ImageEntity) {
        viewModelScope.launch {
            imageDao.insertImage(imageEntity)
        }
    }

    fun getImagesByEmail(email: String, callback: (List<ImageEntity>) -> Unit) {
        viewModelScope.launch {
            val images = imageDao.getImagesByEmail(email)
            callback(images)
        }
    }

    fun updateBookmark(imageId: Int, bookmark: Boolean) {
        viewModelScope.launch {
            imageDao.updateBookmark(imageId, bookmark)
        }
    }

    fun getBookmarkedImagesByEmail(email: String, callback: (List<ImageEntity>) -> Unit) {
        viewModelScope.launch {
            val bookmarkedImages = imageDao.getBookmarkedImagesByEmail(email)
            callback(bookmarkedImages)
        }
    }
}