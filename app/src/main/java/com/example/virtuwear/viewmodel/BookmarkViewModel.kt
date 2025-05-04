package com.example.virtuwear.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.service.SingleGarmentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.net.Uri
import com.example.virtuwear.repository.SingleGarmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


sealed class BookmarkUiState {
    object Loading : BookmarkUiState()
    data class Success(val garments: List<SingleGarmentModel>) : BookmarkUiState()
    data class Error(val message: String) : BookmarkUiState()
}


@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val singleGarmentRepository: SingleGarmentRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<BookmarkUiState>(BookmarkUiState.Loading)
    val uiState: State<BookmarkUiState> = _uiState
    private val _selectedGarment = MutableStateFlow<SingleGarmentModel?>(null)
    val selectedGarment: StateFlow<SingleGarmentModel?> = _selectedGarment

    fun selectGarment(garment: SingleGarmentModel) {
        _selectedGarment.value = garment
    }

    fun getBookmarkedGarment(uid: String) {
        Log.d("BookmarkViewModel", "Memulai permintaan data untuk uid: $uid")

        viewModelScope.launch {
            try {
                val response = singleGarmentRepository.getBookmarked(uid)
                Log.d("BookmarkViewModel", "Response diterima: ${response.code()}")

                if (response.isSuccessful) {
                    val garments = response.body() ?: emptyList()
                    Log.d("BookmarkViewModel", "Jumlah data yang diterima: ${garments.size}")
                    _uiState.value = BookmarkUiState.Success(garments)
                } else {
                    val errorMsg = "Gagal memuat data: ${response.code()} - ${response.message()}"
                    Log.e("BookmarkViewModel", errorMsg)
                    _uiState.value = BookmarkUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val exceptionMsg = "Terjadi kesalahan saat mengambil data: ${e.message}"
                Log.e("BookmarkViewModel", exceptionMsg, e)
                _uiState.value = BookmarkUiState.Error(exceptionMsg)
            }
        }
    }
}