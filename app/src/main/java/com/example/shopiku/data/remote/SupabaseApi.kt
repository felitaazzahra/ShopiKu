package com.example.shopiku.data.remote

import com.example.shopiku.data.model.CartItem
import com.example.shopiku.data.model.Order
import com.example.shopiku.data.model.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface SupabaseApi {

    @GET("rest/v1/products")
    suspend fun getProducts(
        @Query("select") select: String = "*",
        @Query("name") searchName: String? = null
    ): Response<List<Product>>

    @POST("rest/v1/cart")
    suspend fun addToCart(
        @Body cartItem: Map<String, Any>
    ): Response<CartItem>

    @PUT("rest/v1/cart")
    suspend fun updateCartQuantity(
        @Query("id") id: String,
        @Body body: Map<String, Int>
    ): Response<Unit>

    @DELETE("rest/v1/cart")
    suspend fun deleteCartItem(
        @Query("id") id: String
    ): Response<Unit>

    @POST("rest/v1/orders")
    suspend fun createOrder(
        @Body orderData: Map<String, Any>
    ): Response<Order>

    @POST("rest/v1/rpc/simulate_payment")
    suspend fun simulatePayment(
        @Query("order_id") orderId: String
    ): Response<Unit>
}