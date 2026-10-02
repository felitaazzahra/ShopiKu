package com.example.shopiku.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("items") val items: List<CartItem>,
    @SerializedName("address") val address: String,
    @SerializedName("shipping_type") val shippingType: String,
    @SerializedName("shipping_cost") val shippingCost: Double,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("total") val total: Double,
    @SerializedName("status") var status: String, // PENDING_PAYMENT, PAID, PACKED, SHIPPED, DELIVERED
    @SerializedName("va_number") val vaNumber: String = "123 456 7890",
    @SerializedName("expired_at") val expiredAt: String = "",
    @SerializedName("created_at") val createdAt: String = ""
) : Serializable