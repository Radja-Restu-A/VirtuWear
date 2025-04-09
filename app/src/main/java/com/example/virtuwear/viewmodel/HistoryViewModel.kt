package com.example.virtuwear.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.service.SingleGarmentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val garments: List<SingleGarmentModel>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val singleGarmentService: SingleGarmentService
) : ViewModel() {

    private val _uiState = mutableStateOf<HistoryUiState>(HistoryUiState.Loading)
    val uiState: State<HistoryUiState> = _uiState

    fun getAllGarmentByUser(uid: String) {
        Log.d("HistoryViewModel", "Memulai permintaan data untuk uid: $uid")

        viewModelScope.launch {
            try {
                val response = singleGarmentService.getAllGarmentsByUser(uid)
                Log.d("HistoryViewModel", "Response diterima: ${response.code()}")

                if (response.isSuccessful) {
                    val garments = response.body() ?: emptyList()
                    Log.d("HistoryViewModel", "Jumlah data yang diterima: ${garments.size}")
                    _uiState.value = HistoryUiState.Success(garments)
                } else {
                    val errorMsg = "Gagal memuat data: ${response.code()} - ${response.message()}"
                    Log.e("HistoryViewModel", errorMsg)
                    _uiState.value = HistoryUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val exceptionMsg = "Terjadi kesalahan saat mengambil data: ${e.message}"
                Log.e("HistoryViewModel", exceptionMsg, e)
                _uiState.value = HistoryUiState.Error(exceptionMsg)
            }
        }
    }
}