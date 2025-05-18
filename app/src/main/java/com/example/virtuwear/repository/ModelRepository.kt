package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.model.ModelDto
import com.example.virtuwear.data.service.ModelService
import retrofit2.Response
import javax.inject.Inject

class ModelRepository @Inject constructor (
    private val service: ModelService
) {
    suspend fun create(model: ModelDto): Result<ModelDto> {
        return try {
            val response = service.createModel(model)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("ModelRepo", "Create model success: $body")
                    Result.success(body)
                } else {
                    Log.e("ModelRepo", "Create model failed: empty body")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Log.e("ModelRepo", "Create model failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("ModelRepo", "Create model exception: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}