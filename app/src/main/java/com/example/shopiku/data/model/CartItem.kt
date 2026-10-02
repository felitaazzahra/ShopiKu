package com.example.shopiku.data.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String = "user_1",
    @SerializedName("product") val product: Product,
    @SerializedName("variant") val variant: String,
    @SerializedName("quantity") var quantity: Int,
    var isSelected: Boolean = true
) {
    val totalPrice: Double
        get() = product.price * quantity
}