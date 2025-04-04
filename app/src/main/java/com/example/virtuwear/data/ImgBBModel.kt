package com.example.virtuwear.data

import com.google.gson.annotations.SerializedName

data class ImgBBModel(
    @SerializedName("data") val data: ImageData,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int
)

data class ImageData(
    @SerializedName("url") val url: String,
    @SerializedName("url_viewer") val urlViewer: String,
    @SerializedName("thumbnail") val thumbnail: Thumbnail
)

data class Thumbnail(
    @SerializedName("url") val url: String
)