package com.example.virtuwear.data.model

import com.google.gson.annotations.SerializedName

data class ImgBBModel(
    @SerializedName("data") val data: ImageData,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)

data class ImageData(
    @SerializedName("url") val url: String,
    @SerializedName("url_viewer") val urlViewer: String,
    @SerializedName("image") val image: ImageRespon,
)


data class ImageRespon(
    @SerializedName("url") val url: String,
)