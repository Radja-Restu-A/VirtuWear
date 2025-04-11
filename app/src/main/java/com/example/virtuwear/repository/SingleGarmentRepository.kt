package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.model.SingleGarmentModel
import com.example.virtuwear.data.model.SingleGarmentUpdateResult
import com.example.virtuwear.data.service.SingleGarmentService
import retrofit2.Response
import javax.inject.Inject

class SingleGarmentRepository @Inject constructor(
    private val service: SingleGarmentService
) {
    /* yang lain belum ada log nya kalo mau tinggal tambahin :D */
    suspend fun getAllByUser(userId: String) = service.getAllGarmentsByUser(userId)
    suspend fun searchByOutfitName(outfitName: String) = service.searchByOutfitName(outfitName)
    suspend fun getById(id: Long) = service.getSingleGarmentById(id)
    suspend fun create(model: SingleGarmentModel): Result<SingleGarmentModel> {
        return try {
            val response = service.createGarment(model)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("SingleGarmentRepo", "Create single garment success: $body")
                    Result.success(body)
                } else {
                    Log.e("SingleGarmentRepo", "Create single garment failed: empty body")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Log.e("SingleGarmentRepo", "Create single garment failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("SingleGarmentRepo", "Create single garment exception: ${e.localizedMessage}")
            Result.failure(e)
        }
    }



    suspend fun update(id: Long, model: SingleGarmentModel) = service.updateGarment(id, model)
    suspend fun delete(id: Long) = service.deleteGarment(id)

    suspend fun updateModelImage(id: Long, model: SingleGarmentModel): Result<SingleGarmentModel> {
        return try {
            val response = service.updateModelImage(id, model)
            if (response.isSuccessful) {
                Log.d("SingleGarmentRepo", "Update model image success for id=$id")
                Result.success(response.body()!!)
            } else {
                Log.e("SingleGarmentRepo", "Update model image failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("SingleGarmentRepo", "Update model image exception: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    suspend fun updateGarmentImage(id: Long, model: SingleGarmentModel): Result<SingleGarmentModel> {
        return try {
            val response = service.updateGarmentImage(id, model)
            if (response.isSuccessful) {
                Log.d("SingleGarmentRepo", "Update garment image success for id=$id")
                Result.success(response.body()!!)
            } else {
                Log.e("SingleGarmentRepo", "Update garment image failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("SingleGarmentRepo", "Update garment image exception: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    suspend fun updateOutfitName(id: Long, model: SingleGarmentModel) = service.updateOutfitName(id, model)
    suspend fun updateNotes(id: Long, model: SingleGarmentModel) = service.updateNotes(id, model)
    suspend fun updateBookmark(id: Long, model: SingleGarmentModel) = service.updateBookmark(id, model)
}
