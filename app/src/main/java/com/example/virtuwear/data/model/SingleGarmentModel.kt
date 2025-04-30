package com.example.virtuwear.data.model

data class SingleGarmentModel(
    val idSingle: Long? = null,
    val resultImg: String? = null,
    val modelImg: String? = null,
    val garmentImg: String? = null,
    val outfitName: String? = null,
    val notes: String? = null,
    val bookmark: Boolean? = false,
    val userId: String
)

data class SingleGarmentUpdateResult(
    val resultImg: String? = null,
)

data class SingleGarmentUpdateBookmark(
    val isBookmark: Boolean?
)
data class SingleGarmentResponse(
    val idSingle: Long,
    val resultImg: String,
    val modelImg: String,
    val garmentImg: String,
    val outfitName: String,
    val notes: String,
    val bookmark: Boolean,
    val userId: String
)

data class SingleGarmentDto(
    val idSingle: Long? = null,
    val resultImg: String? = null,
    val modelImg: String? = null,
    val garmentImg: String? = null,
    val outfitName: String? = null,
    val notes: String? = null,
    val bookmark: Boolean, // Pastikan nama property tepat
    val userId: String? = null
)

