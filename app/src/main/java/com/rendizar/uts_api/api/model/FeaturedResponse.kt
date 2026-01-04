package com.rendizar.uts_api.api.model

import com.google.gson.annotations.SerializedName

data class FeaturedResponse(
    @SerializedName("topMixes") val topMixes: List<MixItem>,
    @SerializedName("banner") val banner: BannerItem
)

data class MixItem(
    @SerializedName("title") val title: String,
    @SerializedName("imageUrl") val imageUrl: String
)

data class BannerItem(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String
)


