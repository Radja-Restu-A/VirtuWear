package com.example.virtuwear.viewmodel

import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.model.DoubleGarmentModel
import com.example.virtuwear.data.model.DoubleGarmentUpdateBookmark
import com.example.virtuwear.data.model.SingleGarmentDto
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentUpdateBookmark
import com.example.virtuwear.data.service.DoubleGarmentService
import com.example.virtuwear.data.service.SingleGarmentService
import com.example.virtuwear.repository.DoubleGarmentRepository
import com.example.virtuwear.repository.SingleGarmentRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class GarmentViewModel @Inject constructor(
    private val serviceSingle: SingleGarmentService,
    private val singleGarmentRepository: SingleGarmentRepository,
    private val serviceDouble: DoubleGarmentService
) : ViewModel() {

    // State untuk menyimpan ID item yang di-bookmark
    private val _bookmarkedSingleItems = MutableStateFlow<Set<Long>>(setOf())
    val bookmarkedSingleItems: StateFlow<Set<Long>> = _bookmarkedSingleItems

    private val _bookmarkedDoubleItems = MutableStateFlow<Set<Long>>(setOf())
    val bookmarkedDoubleItems: StateFlow<Set<Long>> = _bookmarkedDoubleItems

    // State untuk loading dan error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Inisialisasi data bookmark dari server
    init {
        loadBookmarkedItems()
    }
    val firebase = FirebaseAuth.getInstance().currentUser
    val user = firebase?.uid
    val uid = user
    // Fungsi untuk memuat data bookmark dari server
    private fun loadBookmarkedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load bookmarked single items
                val singleBookmarks = uid?.let { singleGarmentRepository.getBookmarked(it) }
                if (singleBookmarks != null) {
                    if (singleBookmarks.isSuccessful) {
                        _bookmarkedSingleItems.value =
                            (singleBookmarks.body()?.map { it.idSingle }?.toSet() ?: setOf()) as Set<Long>
                    }
                }

                // Load bookmarked double items
                val doubleBookmarks = serviceDouble.getBookmarkedItems()
                if (doubleBookmarks.isSuccessful) {
                    _bookmarkedDoubleItems.value = doubleBookmarks.body()?.map { it.idDouble }?.toSet() ?: setOf()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data bookmark: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk mengecek apakah item di-bookmark
    fun isItemBookmarked(id: Long, isSingle: Boolean): Boolean {
        return if (isSingle) {
            _bookmarkedSingleItems.value.contains(id)
        } else {
            _bookmarkedDoubleItems.value.contains(id)
        }
    }

    // Update bookmark untuk single item
    fun updateSingleBookmark(id: Long, isBookmarked: Boolean) {
        Log.d("GarmentViewModel", "Requesting bookmark update - Single id: $id isBookmarked: $isBookmarked")

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Log request details
                val bookmarkRequest = SingleGarmentUpdateBookmark(isBookmark = isBookmarked)
                Log.d("GarmentViewModel", "Request body: $bookmarkRequest")

                val dto = SingleGarmentDto(bookmark = isBookmarked)
                val response = serviceSingle.updateBookmark(id, dto)

                // Log response details
                Log.d("GarmentViewModel", "Response code: ${response.code()}")
                Log.d("GarmentViewModel", "Response message: ${response.message()}")
                Log.d("GarmentViewModel", "Response body: ${response.body()}")
                Log.d("GarmentViewModel", "Response error body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    // Update local state
                    val currentBookmarks = _bookmarkedSingleItems.value.toMutableSet()
                    if (isBookmarked) {
                        currentBookmarks.add(id)
                    } else {
                        currentBookmarks.remove(id)
                    }
                    _bookmarkedSingleItems.value = currentBookmarks
                    Log.d("GarmentViewModel", "Local state updated, bookmarked items: ${_bookmarkedSingleItems.value}")
                } else {
                    _errorMessage.value = "Gagal mengupdate bookmark: ${response.message()}"
                    Log.e("GarmentViewModel", "Failed to update bookmark: ${response.code()}, message: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("GarmentViewModel", "Exception during bookmark update", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Update bookmark untuk double item
    fun updateDoubleBookmark(id: Long, isBookmarked: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = serviceDouble.updateBookmark(
                    id,
                    DoubleGarmentUpdateBookmark(isBookmark = isBookmarked)
                )

                if (response.isSuccessful) {
                    // Update local state
                    val currentBookmarks = _bookmarkedDoubleItems.value.toMutableSet()
                    if (isBookmarked) {
                        currentBookmarks.add(id)
                    } else {
                        currentBookmarks.remove(id)
                    }
                    _bookmarkedDoubleItems.value = currentBookmarks
                    Log.d("GarmentViewModel", "Double bookmark updated successfully")
                } else {
                    _errorMessage.value = "Gagal mengupdate bookmark: ${response.message()}"
                    Log.e("GarmentViewModel", "Failed to update bookmark: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}