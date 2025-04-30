package com.example.virtuwear.viewmodel

import androidx.lifecycle.ViewModel
import com.example.virtuwear.data.model.SingleGarmentResponse
import com.example.virtuwear.repository.SingleGarmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.sin

@HiltViewModel
class GarmentDetailViewModel @Inject constructor(
    private val singleGarmentRepository: SingleGarmentRepository
): ViewModel() {
    suspend fun deleteGarment(id: Long){
        singleGarmentRepository.delete(id)
    }
    suspend fun getById(id: Long): Response<SingleGarmentResponse> {
        return singleGarmentRepository.getById(id)
    }
}