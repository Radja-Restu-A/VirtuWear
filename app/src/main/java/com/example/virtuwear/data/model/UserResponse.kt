package com.example.virtuwear.data.model

data class Referral(
    val referralCode: String,
    val totalUsed: Long,
    val cooldown: String
)

data class UserResponse(
    val uid: String,
    val email: String,
    val name: String,
    val token: Int,
    val totalTryon: Int,
    val totalGenerate: Int,
    val redeemedReferral: String,
    val referral: Referral
)