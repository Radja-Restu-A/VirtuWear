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

//    suspend fun pollResult(taskId: String): String? {
//        repeat(10) { attempt ->
//            val res = service.getTaskStatus(taskId)
//            if (res.isSuccessful) {
//                val responseBody = res.body()
//                val status = responseBody?.data?.task_status
//                val statusMsg = responseBody?.data?.task_status_msg
//
//                Log.d("VTO Polling", "Attempt $attempt - Status: $status - Message: $statusMsg")
//
//                // Berhenti kalau sukses
//                if (status == "succeed") {
//                    val resultUrl = responseBody.data.task_result?.images?.firstOrNull()?.url
//                    if (resultUrl != null) {
//                        return resultUrl
//                    } else {
//                        Log.e("VTO Polling", "Task succeeded but no result image found.")
//                        return null
//                    }
//                }
//
//                // Berhenti kalau gagal
//                if (statusMsg == "failed") {
//                    Log.e("VTO Polling", "Task failed: $statusMsg")
//                    return null
//                }
//            } else {
//                Log.e("VTO Polling", "Failed to fetch task status: ${res.code()}")
//            }
//
//            delay(30_000)
//        }
//
//        Log.e("VTO Polling", "Polling finished. Task did not complete in time.")
//        return null
//    }

//    suspend fun getPolledResultFromBackend(taskId: String): String? {
//        val response = service.getTryOnTaskStatusWithPolling(taskId)
//        if (response.isSuccessful) {
//            val data = response.body()?.data
//            val resultUrl = data?.task_result?.images?.firstOrNull()?.url
//            if (resultUrl != null) {
//                Log.d("VTO Backend Poll", "Received image URL from backend: $resultUrl")
//                return resultUrl
//            } else {
//                Log.e("VTO Backend Poll", "No image found in backend polling result")
//            }
//        } else {
//            Log.e("VTO Backend Poll", "Failed to poll from backend: ${response.code()}")
//        }
//        return null
//    }

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