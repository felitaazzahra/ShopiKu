package com.example.shopiku.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String = "081234567890",
    @SerializedName("address") val address: String = "Jl. Kartini No. 45, Kesambi, Kota Cirebon, Jawa Barat",
    @SerializedName("avatar_url") val avatarUrl: String = ""
)