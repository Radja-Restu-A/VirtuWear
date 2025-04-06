package com.example.virtuwear.data.model

data class KlingAiRequestDto(
    val model_name: String,
    val human_image: String,
    val cloth_image: String,
    val callback_url: String
)

data class KlingAiCreateResponseDto(
    val code: Int,
    val message: String,
    val request_id: String,
    val data: DataDto
)

data class DataDto(
    val task_id: String,
    val task_status: String,
    val created_at: Long,
    val updated_at: Long
)
