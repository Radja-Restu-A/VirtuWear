package com.example.virtuwear.data.model

data class SingleGarmentModel(
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


