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


sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val garments: List<SingleGarmentModel>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val singleGarmentService: SingleGarmentService,
    private val singleGarmentRepository: SingleGarmentRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<HistoryUiState>(HistoryUiState.Loading)
    val uiState: State<HistoryUiState> = _uiState
    private val _selectedGarment = MutableStateFlow<SingleGarmentModel?>(null)

    fun selectGarment(garment: SingleGarmentModel) {
        _selectedGarment.value = garment
    }

    fun sortingByDate(timeMillis: Long) {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            try {
                val response = singleGarmentService.findByCreatedAt(timeMillis)
                if (response.isSuccessful) {
                    val garments = response.body() ?: emptyList()
                    _uiState.value = HistoryUiState.Success(garments)
                } else {
                    _uiState.value = HistoryUiState.Error("There is no image from this date")
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error("Sorry we couldn't find your image")
            }
        }
    }


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


    fun searchGarment(outfitName: String) {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            try {
                val encoded = Uri.encode(outfitName)
                val response = singleGarmentService.searchByOutfitName(encoded)
                if (response.isSuccessful) {
                    val garments = response.body() ?: emptyList()
                    _uiState.value = HistoryUiState.Success(garments)
                } else {
                    Log.e("HistoryViewModel", "Gagal search: ${response.code()}")
                    _uiState.value = HistoryUiState.Error("Gagal search: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Exception search: ${e.message}")
                _uiState.value = HistoryUiState.Error("Exception: ${e.message}")
            }
        }
    }
}