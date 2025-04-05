package com.example.virtuwear.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_table",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["email"],
            childColumns = ["emailOwner"],
            onDelete = ForeignKey.CASCADE // Hapus gambar jika profil dihapus
        )
    ],
    indices = [Index(value = ["emailOwner"])]
)
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Tambahkan ID sebagai primary key
    val gambarBody: String,
    val gambarPakaian: String,
    val gambarHasil: String? = null,
    val bookmark: Boolean,
    val emailOwner: String, // Relasi ke tabel Profile
    val gambarke: Int
)
