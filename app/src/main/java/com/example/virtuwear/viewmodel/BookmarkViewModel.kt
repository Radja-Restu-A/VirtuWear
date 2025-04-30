package com.example.virtuwear.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.service.SingleGarmentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val singleGarmentService: SingleGarmentService
) : ViewModel() {

    private val _uiState = mutableStateOf<HistoryUiState>(HistoryUiState.Loading)
    val uiState: State<HistoryUiState> = _uiState

    fun getAllGarmentByUser(uid: String) {
        Log.d("BookmarkViewModel", "Memulai permintaan bookmark data untuk uid: $uid")

        viewModelScope.launch {
            try {
                val response = singleGarmentService.getAllGarmentsByUser(uid)
                Log.d("BookmarkViewModel", "Response diterima: ${response.code()}")

                if (response.isSuccessful) {
                    val garments = response.body() ?: emptyList()
                    Log.d("BookmarkViewModel", "Jumlah data yang diterima: ${garments.size}")
                    _uiState.value = HistoryUiState.Success(garments)
                } else {
                    val errorMsg = "Gagal memuat data: ${response.code()} - ${response.message()}"
                    Log.e("BookmarkViewModel", errorMsg)
                    _uiState.value = HistoryUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val exceptionMsg = "Terjadi kesalahan saat mengambil data: ${e.message}"
                Log.e("BookmarkViewModel", exceptionMsg, e)
                _uiState.value = HistoryUiState.Error(exceptionMsg)
            }
        }
    }
}
