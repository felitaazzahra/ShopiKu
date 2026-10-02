package com.example.shopiku.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("category_id") val categoryId: String = "cat_1",
    @SerializedName("category_name") val categoryName: String = "Sneakers",
    @SerializedName("rating") val rating: Double = 4.8,
    @SerializedName("sold_count") val soldCount: Int = 120,
    @SerializedName("stock") val stock: Int = 25,
    @SerializedName("variants") val variants: List<Variant> = emptyList()
) : Serializable