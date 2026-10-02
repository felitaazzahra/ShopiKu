package com.example.shopiku.data.model

import com.google.gson.annotations.SerializedName

data class Variant(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("stock") val stock: Int = 10
)