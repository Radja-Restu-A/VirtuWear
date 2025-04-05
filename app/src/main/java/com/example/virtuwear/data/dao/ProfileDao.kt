package com.example.virtuwear.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.virtuwear.data.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Insert
    suspend fun insert(profile: ProfileEntity)

    @Query("SELECT * FROM profile_table WHERE email = :email")
    suspend fun getProfileByEmail(email: String): ProfileEntity?

    @Query("UPDATE profile_table SET koin = :koin WHERE email = :email")
    suspend fun updateKoinByEmail(email: String, koin: Int)
}