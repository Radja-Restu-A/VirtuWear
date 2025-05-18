package com.example.virtuwear.data.model

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: String
)