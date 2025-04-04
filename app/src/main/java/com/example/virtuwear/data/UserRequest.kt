package com.example.virtuwear.data

data class Referral(
    val referralCode: String = "-",
    val totalUsed: Long = 0,
    val cooldown: String = "2024-03-30T12:00:00"
)

data class UserRequest(
    val uid: String,
    val email: String,
    val name: String,
    val token: Int = 0,
    val totalTryon: Int = 0,
    val totalGenerate: Int = 0,
    val redeemedReferral: String = "-",
    val referral: Referral = Referral()
)