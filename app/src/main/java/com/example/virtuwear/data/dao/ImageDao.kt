package com.example.virtuwear.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.virtuwear.data.entity.ImageEntity

@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(imageEntity: ImageEntity)

    @Query("SELECT * FROM image_table WHERE emailOwner = :email")
    suspend fun getImagesByEmail(email: String): List<ImageEntity>

    @Query("UPDATE image_table SET bookmark = :bookmark WHERE id = :imageId")
    suspend fun updateBookmark(imageId: Int, bookmark: Boolean)

    @Query("SELECT * FROM image_table WHERE bookmark = 1 AND emailOwner = :email")
    suspend fun getBookmarkedImagesByEmail(email: String): List<ImageEntity>

}