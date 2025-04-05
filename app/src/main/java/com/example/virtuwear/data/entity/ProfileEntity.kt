package com.example.virtuwear.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class ProfileEntity(
    @PrimaryKey val email: String,
    val koin: Int
)