package com.example.virtuwear.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentResponse
import com.example.virtuwear.repository.SingleGarmentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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

    fun getInfoId() : FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun updateNotes(id: Long, notesInput:String) {
        viewModelScope.launch {
            snapshotFlow { notesInput }
                .debounce(500)
                .distinctUntilChanged()
                .collect {
                    val model = SingleGarmentModel(
                        id = id,
                        note = notesInput,
                        userUid = getInfoId()?.uid ?: ""
                    )
                    Log.e(notesInput, "pesan kekirim")
                    singleGarmentRepository.updateNotes(id, model)
                }
        }
    }
}