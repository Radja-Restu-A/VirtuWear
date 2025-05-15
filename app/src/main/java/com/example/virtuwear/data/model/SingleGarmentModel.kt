package com.example.virtuwear.data.model

data class SingleGarmentModel(
    val id: Long? = null,
    val resultImage: String? = null,
    val modelImage: String? = null,
    val garmentImage: String? = null,
    val outfitName: String? = null,
    val notes: String? = null,
    val bookmark: Boolean? = false,
    val userUid: String
)

data class SingleGarmentUpdateResult(
    val resultImage: String? = null,
)

data class SingleGarmentUpdateBookmark(
    val isBookmark: Boolean?
)
data class SingleGarmentResponse(
    val id: Long,
    val resultImage: String,
    val modelImage: String,
    val garmentImage: String,
    val outfitName: String,
    val notes: String,
    val bookmark: Boolean,
    val userUid: String
)

data class SingleGarmentDto(
    val id: Long? = null,
    val resultImage: String? = null,
    val modelImage: String? = null,
    val garmentImage: String? = null,
    val outfitName: String? = null,
    val notes: String? = null,
    val bookmark: Boolean,
    val userUid: String? = null
)

