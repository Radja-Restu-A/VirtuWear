package com.example.virtuwear.data.model

import java.sql.Timestamp

data class Referral(
    val referralCode: String,
    val totalUsed: Long,
    val milestone: Int,

    )
data class Coin(
    val coinId: Long,
    val coinBalance: Integer,
    val validUntil: Timestamp
)

data class UserResponse(
    val uid: String,
    val email: String,
    val totalGenerate: Int,
    val redeemedReferral: String,
    val referral: Referral,
    val coin: Coin
)