package com.example.virtuwear.data.model

data class SingleGarmentModel(
//    val idSingle: String? = null,
//    val userId: String,
//    val outfitName: String? = null,
//    val notes: String? = null,
//    val isBookmarked: Boolean = false,
//    val garmentImageUrl: String? = null,
//    val modelImageUrl: String? = null,
//    val resultImageUrl: String? = null

    val idSingle: Long? = null,
    val resultImg: String? = null,
    val modelImg: String? = null,
    val garmentImg: String? = null,
    val outfitName: String? = null,
    val notes: String? = null,
    val isBookmark: Boolean = false,
    val userId: String
)

data class SingleGarmentUpdateResult(
    val resultImg: String? = null,
)


