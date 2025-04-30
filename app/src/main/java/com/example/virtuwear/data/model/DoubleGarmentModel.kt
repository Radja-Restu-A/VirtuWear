package com.example.virtuwear.data.model


data class DoubleGarmentModel(
    val idDouble: Long? = null,
    val resultImg: String? = null,
    val modelImg: String? = null,
    val topImg: String? = null,
    val bottomImg: String? = null,
    val outfitName: String? = null,
    val notes: String? = null,
    val isBookmark: Boolean? = false,
    val userId: String
)

data class DoubleGarmentUpdateBookmark(
    val isBookmark: Boolean?
)

data class DoubleGarmentResponse(
    val idDouble: Long,
    val resultImg: String,
    val modelImg: String,
    val topImg: String,
    val bottomImg: String,
    val outfitName: String,
    val notes: String,
    val isBookmark: Boolean,
    val userId: String
)