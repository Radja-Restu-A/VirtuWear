package com.example.virtuwear.data.model

//enum class TransactionType {
//    PURCHASE, SPEND, REWARD
//}

data class TransactionDto(
    val transactionId: Long,
    val coinId: Long,
    val transactionType: String,
    val amount: Int
)