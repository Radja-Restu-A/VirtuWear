package com.example.virtuwear.repository

import android.util.Log
import com.example.virtuwear.data.model.KlingAiRequestDto
import com.example.virtuwear.data.service.KlingAiApiService
import com.example.virtuwear.data.service.SingleGarmentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KlingAiRepository @Inject constructor (
    private val service: KlingAiApiService
) {
    suspend fun createTryOn(
        request: KlingAiRequestDto,
    ): String? {
        val response = service.createTryOn(request)
        if (response.isSuccessful) {
            return response.body()?.data?.task_id
        }
        return null
    }

    suspend fun pollKlingApiUntilComplete(taskId: String): String? {
        repeat(10) { attempt ->
            try {
                val response = service.getTryOnTaskStatus(taskId)
                if (response.isSuccessful) {
                    val taskData = response.body()?.data
                    val status = taskData?.task_status

                    when (status) {
                        "succeed" -> {
                            val resultUrl = taskData.task_result?.images?.firstOrNull()?.url
                            Log.d("VTO", "Polling succeed at attempt $attempt: $resultUrl")
                            return resultUrl
                        }
                        "failed" -> {
                            Log.e("VTO", "Polling failed: ${taskData.task_status_msg}")
                            return null
                        }
                        else -> {
                            Log.d("VTO", "Polling... Attempt $attempt, status: $status")
                        }
                    }
                } else {
                    Log.e("VTO", "Polling failed: ${response.code()}")
                }

                delay(30_000) // Tunggu 30 detik sebelum attempt berikutnya

            } catch (e: Exception) {
                Log.e("VTO", "Polling error: ${e.message}", e)
            }
        }

        Log.e("VTO", "Polling reached max retries.")
        return null
    }




}