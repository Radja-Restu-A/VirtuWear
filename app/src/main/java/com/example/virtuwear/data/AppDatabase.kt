package com.example.virtuwear.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.virtuwear.data.dao.ImageDao
import com.example.virtuwear.data.dao.ProfileDao
import com.example.virtuwear.data.entity.ImageEntity
import com.example.virtuwear.data.entity.ProfileEntity

@Database(entities = [ProfileEntity::class, ImageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val imageDao: ImageDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}