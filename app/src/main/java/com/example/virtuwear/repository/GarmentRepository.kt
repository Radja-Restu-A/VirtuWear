package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.model.GarmentDto
import com.example.virtuwear.data.model.ModelDto
import com.example.virtuwear.data.service.GarmentService
import javax.inject.Inject

class GarmentRepository @Inject constructor (
    private val service: GarmentService
) {
    suspend fun create(garment: GarmentDto): Result<GarmentDto> {
        return try {
            val response = service.createGarment(garment)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("GarmentRepo", "Create garment success: $body")
                    Result.success(body)
                } else {
                    Log.e("GarmentRepo", "Create garment failed: empty body")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Log.e("GarmentRepo", "Create garment failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("GarmentRepo", "Create garment exception: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}