package com.example.shopiku.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_avatar") val userAvatar: String = "",
    @SerializedName("rating") val rating: Float,
    @SerializedName("comment") val comment: String,
    @SerializedName("variant_info") val variantInfo: String = "Varian: Hitam / 42",
    @SerializedName("created_at") val createdAt: String
)