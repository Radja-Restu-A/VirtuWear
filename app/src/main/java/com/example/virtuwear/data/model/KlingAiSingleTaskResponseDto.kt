package com.example.virtuwear.data.model

data class KlingAiSingleTaskResponseDto(
    val code: Int,
    val message: String,
    val request_id: String,
    val data: TaskData
)

data class TaskData(
    val task_id: String,
    val task_status: String,
    val task_status_msg: String?,
    val created_at: Long,
    val updated_at: Long,
    val task_result: TaskResult? = null
)

data class TaskResult(
    val images: List<TaskImage>
)

data class TaskImage(
    val index: Int,
    val url: String
)
